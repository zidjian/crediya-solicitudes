package co.com.crediya.api.error;

import co.com.crediya.shared.error.ErrorDetail;
import co.com.crediya.shared.error.ErrorResponse;
import co.com.crediya.usecase.solicitud.exceptions.BusinessException;
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
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@Order(-2)
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorHandler(ErrorAttributes errorAttributes,
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
        String mensaje = error.getMessage() != null ? error.getMessage() : "Error inesperado";

        ErrorResponse response = ErrorResponse.of(
                codigo,
                mensaje,
                status.value(),
                request.path(),
                buildDetalles(error),
                UUID.randomUUID().toString()
        );

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
        if (error instanceof WebExchangeBindException || isJacksonDeserializationError(error)) {
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
        if (error instanceof WebExchangeBindException || isJacksonDeserializationError(error)) {
            return "VALIDATION_ERROR";
        }
        return "ERR-500";
    }

    private List<ErrorDetail> buildDetalles(Throwable error) {
        if (error instanceof WebExchangeBindException bindException) {
            return bindException.getFieldErrors().stream()
                    .map(fieldError -> new ErrorDetail(
                            fieldError.getField(),
                            fieldError.getDefaultMessage()))
                    .toList();
        }
        if (isJacksonDeserializationError(error)) {
            String fieldName = extractFieldFromMessage(error.getCause().getMessage());
            return List.of(new ErrorDetail(fieldName, "Propiedad desconocida no permitida"));
        }
        return Collections.emptyList();
    }

    private boolean isJacksonDeserializationError(Throwable error) {
        return error.getCause() != null &&
                error.getCause().getMessage() != null &&
                error.getCause().getMessage().contains("Unrecognized field");
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
}
