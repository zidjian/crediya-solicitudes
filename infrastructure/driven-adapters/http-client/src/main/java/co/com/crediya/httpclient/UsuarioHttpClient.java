package co.com.crediya.httpclient;

import co.com.crediya.httpclient.dto.UsuarioResponseDTO;
import co.com.crediya.httpclient.exception.HttpClientExceptionHandler;
import co.com.crediya.model.usuario.gateways.UsuarioValidacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsuarioHttpClient implements UsuarioValidacionRepository {

    private final WebClient webClient;
    private final HttpClientExceptionHandler exceptionHandler;

    @Value("${microservices.usuarios.base-url:http://localhost:8080}")
    private String usuariosBaseUrl;

    private static final String USUARIOS_ENDPOINT = "/api/v1/usuarios/documento/{documento}";

    @Override
    public Mono<Boolean> existeUsuarioPorDocumento(String documentoIdentidad) {
        log.info("Validando existencia de usuario con documento: {}", documentoIdentidad);

        return webClient
                .get()
                .uri(usuariosBaseUrl + USUARIOS_ENDPOINT, documentoIdentidad)
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .doOnNext(usuario -> log.debug("Usuario encontrado - ID: {}, Activo: {}",
                         usuario.getIdUsuario(), usuario.isActivo()))
                .map(this::esUsuarioValido)
                .doOnSuccess(existe -> log.info("Usuario con documento {} existe y es válido: {}",
                           documentoIdentidad, existe))
                .onErrorResume(WebClientResponseException.class, ex ->
                    exceptionHandler.handleWebClientException(ex, documentoIdentidad))
                .onErrorResume(Exception.class, ex ->
                    exceptionHandler.handleGenericException(ex, documentoIdentidad));
    }

    private boolean esUsuarioValido(UsuarioResponseDTO usuario) {
        if (usuario == null) {
            log.warn("Usuario response es null");
            return false;
        }

        return true;
    }
}

