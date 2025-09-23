package co.com.crediya.httpclient.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HttpClientExceptionHandler {

  public Mono<Boolean> handleWebClientException(WebClientResponseException ex, String documento) {
    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
      log.info("Usuario con documento {} no encontrado", documento);
      return Mono.just(false);
    }

    if (ex.getStatusCode().is4xxClientError()) {
      log.warn(
          "Error del cliente al consultar usuario con documento {} - Status: {}",
          documento,
          ex.getStatusCode());
      return Mono.just(false);
    }

    log.error(
        "Error del servidor al consultar usuario con documento {} - Status: {}, Message: {}",
        documento,
        ex.getStatusCode(),
        ex.getMessage());

    return Mono.error(
        new UsuarioValidacionException(
            String.format(
                "Error al validar usuario con documento %s - Status: %s",
                documento, ex.getStatusCode()),
            ex));
  }

  public Mono<Boolean> handleGenericException(Exception ex, String documento) {
    log.error(
        "Error inesperado al consultar usuario con documento {}: {}",
        documento,
        ex.getMessage(),
        ex);

    return Mono.error(
        new UsuarioValidacionException(
            String.format("Error inesperado al validar usuario con documento %s", documento), ex));
  }
}
