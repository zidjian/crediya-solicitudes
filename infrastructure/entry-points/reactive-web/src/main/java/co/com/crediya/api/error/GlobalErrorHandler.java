package co.com.crediya.api.error;

import co.com.crediya.shared.error.ErrorDetail;
import co.com.crediya.shared.error.ErrorResponse;
import co.com.crediya.usecase.solicitud.exceptions.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@Order(-2)
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

  public GlobalErrorHandler(
      ErrorAttributes errorAttributes,
      ApplicationContext applicationContext,
      ServerCodecConfigurer codecConfigurer) {
    super(errorAttributes, new WebProperties.Resources(), applicationContext);
    this.setMessageWriters(codecConfigurer.getWriters());
    this.setMessageReaders(codecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = getError(request);
    HttpStatus status = determineHttpStatus(error);
    String codigo = determineCodigo(error);
    String mensaje = determineMensaje(error);

    ErrorResponse response =
        ErrorResponse.of(
            codigo,
            mensaje,
            status.value(),
            request.path(),
            buildDetalles(error),
            UUID.randomUUID().toString());

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(response);
  }

  private HttpStatus determineHttpStatus(Throwable error) {
    if (error instanceof BusinessException businessException) {
      return HttpStatus.valueOf(businessException.getHttpStatus());
    }
    if (error instanceof ResponseStatusException ex) {
      return (HttpStatus) ex.getStatusCode();
    }
    if (error instanceof WebExchangeBindException
        || error instanceof jakarta.validation.ConstraintViolationException
        || isJacksonDeserializationError(error)
        || isJsonProcessingError(error)
        || isServerWebInputException(error)) {
      return HttpStatus.BAD_REQUEST;
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  private String determineCodigo(Throwable error) {
    if (error instanceof BusinessException businessException) {
      return businessException.getCode();
    }
    if (error instanceof ResponseStatusException ex) {
      return String.valueOf(ex.getStatusCode().value());
    }
    if (error instanceof WebExchangeBindException
        || error instanceof jakarta.validation.ConstraintViolationException
        || isJacksonDeserializationError(error)
        || isJsonProcessingError(error)
        || isServerWebInputException(error)) {
      return "400";
    }
    return "ERR-500";
  }

  private String determineMensaje(Throwable error) {
    if (isJsonProcessingError(error) || isServerWebInputException(error)) {
      return "Error en el formato de los datos enviados";
    }
    if (error instanceof WebExchangeBindException
        || error instanceof jakarta.validation.ConstraintViolationException) {
      return "Error de validación en los datos enviados";
    }
    return error.getMessage() != null ? error.getMessage() : "Error inesperado";
  }

  private List<ErrorDetail> buildDetalles(Throwable error) {
    if (error instanceof WebExchangeBindException bindException) {
      return bindException.getFieldErrors().stream()
          .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
          .toList();
    }

    // Agregar manejo para ConstraintViolationException
    if (error instanceof jakarta.validation.ConstraintViolationException constraintException) {
      return constraintException.getConstraintViolations().stream()
          .map(
              violation ->
                  new ErrorDetail(violation.getPropertyPath().toString(), violation.getMessage()))
          .toList();
    }

    // Manejar errores de deserialización JSON específicos
    if (isJsonProcessingError(error)) {
      return handleJsonProcessingError(error);
    }

    if (isServerWebInputException(error)) {
      return handleServerWebInputException(error);
    }

    if (isJacksonDeserializationError(error)) {
      String fieldName = extractFieldFromMessage(error.getCause().getMessage());
      return List.of(new ErrorDetail(fieldName, "Propiedad desconocida no permitida"));
    }

    // Extraer detalles del mensaje si contiene información de campo
    if (error.getMessage() != null && error.getMessage().contains(":")) {
      return extractDetailsFromMessage(error.getMessage());
    }

    return Collections.emptyList();
  }

  private boolean isJsonProcessingError(Throwable error) {
    Throwable cause = error.getCause();
    return cause instanceof JsonProcessingException
        || cause instanceof InvalidFormatException
        || cause instanceof MismatchedInputException;
  }

  private boolean isServerWebInputException(Throwable error) {
    return error instanceof ServerWebInputException;
  }

  private List<ErrorDetail> handleJsonProcessingError(Throwable error) {
    Throwable cause = error.getCause();

    if (cause instanceof InvalidFormatException invalidFormatException) {
      String fieldName = extractFieldNameFromJsonReference(invalidFormatException.getPath());
      String targetType = invalidFormatException.getTargetType().getSimpleName();
      String value = invalidFormatException.getValue().toString();

      String mensaje =
          String.format(
              "El valor '%s' no es válido para el campo de tipo %s",
              value, getSpanishTypeName(targetType));

      return List.of(new ErrorDetail(fieldName, mensaje));
    }

    if (cause instanceof MismatchedInputException mismatchedInputException) {
      String fieldName = extractFieldNameFromJsonReference(mismatchedInputException.getPath());
      String targetType = mismatchedInputException.getTargetType().getSimpleName();

      String mensaje =
          String.format(
              "El formato del campo no es válido para tipo %s", getSpanishTypeName(targetType));

      return List.of(new ErrorDetail(fieldName, mensaje));
    }

    return List.of(new ErrorDetail("unknown", "Error de formato en el JSON"));
  }

  private List<ErrorDetail> handleServerWebInputException(Throwable error) {
    ServerWebInputException serverError = (ServerWebInputException) error;
    Throwable rootCause = getRootCause(serverError);

    if (rootCause instanceof InvalidFormatException invalidFormatException) {
      String fieldName = extractFieldNameFromJsonReference(invalidFormatException.getPath());
      String targetType = invalidFormatException.getTargetType().getSimpleName();
      String value = invalidFormatException.getValue().toString();

      String mensaje =
          String.format(
              "El valor '%s' no es válido para el campo de tipo %s",
              value, getSpanishTypeName(targetType));

      return List.of(new ErrorDetail(fieldName, mensaje));
    }

    return List.of(new ErrorDetail("request", "Error en el formato de la petición"));
  }

  private String extractFieldNameFromJsonReference(List<JsonMappingException.Reference> path) {
    if (path != null && !path.isEmpty()) {
      return path.get(path.size() - 1).getFieldName();
    }
    return "unknown";
  }

  private String getSpanishTypeName(String javaTypeName) {
    return switch (javaTypeName.toLowerCase()) {
      case "bigdecimal" -> "decimal";
      case "integer", "int" -> "número entero";
      case "long" -> "número entero largo";
      case "double", "float" -> "número decimal";
      case "boolean" -> "booleano";
      case "localdate" -> "fecha";
      case "localdatetime" -> "fecha y hora";
      default -> javaTypeName;
    };
  }

  private Throwable getRootCause(Throwable throwable) {
    Throwable cause = throwable.getCause();
    if (cause == null) {
      return throwable;
    }
    return getRootCause(cause);
  }

  private boolean isJacksonDeserializationError(Throwable error) {
    return error.getCause() != null
        && error.getCause().getMessage() != null
        && error.getCause().getMessage().contains("Unrecognized field");
  }

  private String extractFieldFromMessage(String message) {
    int start = message.indexOf("\"");
    if (start != -1) {
      int end = message.indexOf("\"", start + 1);
      if (end > start) {
        return message.substring(start + 1, end);
      }
    }
    return "unknown";
  }

  private List<ErrorDetail> extractDetailsFromMessage(String message) {
    // Buscar patrón "campo: mensaje"
    String[] parts = message.split(":", 2);
    if (parts.length == 2) {
      String field = parts[0].trim();
      String errorMessage = parts[1].trim();
      return List.of(new ErrorDetail(field, errorMessage));
    }
    return Collections.emptyList();
  }
}
