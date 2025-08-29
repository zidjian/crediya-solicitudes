package co.com.crediya.httpclient;

import co.com.crediya.httpclient.dto.UsuarioResponseDTO;
import co.com.crediya.model.usuario.gateways.UsuarioValidacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UsuarioHttpClient implements UsuarioValidacionRepository {

    private final WebClient webClient;
    private final String usuariosBaseUrl;

    public UsuarioHttpClient(WebClient webClient,
                           @Value("${microservices.usuarios.base-url:http://localhost:8080}") String usuariosBaseUrl) {
        this.webClient = webClient;
        this.usuariosBaseUrl = usuariosBaseUrl;
    }

    @Override
    public Mono<Boolean> existeUsuarioPorDocumento(String documentoIdentidad) {
        log.info("Validando existencia de usuario con documento: {}", documentoIdentidad);

        return webClient
                .get()
                .uri(usuariosBaseUrl + "/api/v1/usuarios/documento/{documento}", documentoIdentidad)
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .map(usuario -> usuario != null && usuario.isActivo())
                .doOnSuccess(existe -> log.info("Usuario con documento {} existe: {}", documentoIdentidad, existe))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.info("Usuario con documento {} no encontrado", documentoIdentidad);
                        return Mono.just(false);
                    }
                    log.error("Error al consultar usuario con documento {}: {}", documentoIdentidad, ex.getMessage());
                    return Mono.error(new RuntimeException("Error al validar usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al consultar usuario con documento {}: {}", documentoIdentidad, ex.getMessage());
                    return Mono.error(new RuntimeException("Error interno al validar usuario: " + ex.getMessage()));
                });
    }
}
