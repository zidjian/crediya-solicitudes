package co.com.crediya.httpclient;

import co.com.crediya.httpclient.dto.UsuarioResponseDTO;
import co.com.crediya.httpclient.exception.HttpClientExceptionHandler;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.model.usuario.gateways.UsuarioValidacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsuarioHttpClient implements UsuarioRepository, UsuarioValidacionRepository {

  private final WebClient webClient;
  private final HttpClientExceptionHandler exceptionHandler;

  @Value("${microservices.usuarios.base-url:http://localhost:8080}")
  private String usuariosBaseUrl;

  @Value("${microservices.usuarios.token:}")
  private String usuariosToken;

  private static final String USUARIOS_BY_DOCUMENTO_ENDPOINT =
      "/api/v1/usuarios/documento/{documento}";
  private static final String USUARIOS_BY_ID_ENDPOINT = "/api/v1/usuarios/{id}";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  public Mono<Usuario> obtenerUsuarioPorId(Long idUsuario) {
    log.info("Obteniendo usuario por ID: {}", idUsuario);

    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(auth -> auth != null ? (String) auth.getCredentials() : null)
        .defaultIfEmpty(usuariosToken)
        .flatMap(
            token ->
                webClient
                    .get()
                    .uri(usuariosBaseUrl + USUARIOS_BY_ID_ENDPOINT, idUsuario)
                    .headers(
                        headers -> {
                          if (token != null && !token.isEmpty()) {
                            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
                          }
                        })
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .doOnNext(
                        usuario ->
                            log.debug(
                                "Usuario encontrado - ID: {}, Documento: {}",
                                usuario.getIdUsuario(),
                                usuario.getDocumentoIdentidad()))
                    .map(this::mapToUsuario)
                    .doOnSuccess(
                        usuario -> log.info("Usuario con ID {} obtenido exitosamente", idUsuario))
                    .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> {
                          log.warn("Usuario con ID {} no encontrado", idUsuario);
                          return Mono.empty();
                        })
                    .onErrorResume(
                        WebClientResponseException.class,
                        ex -> {
                          log.error(
                              "Error al obtener usuario con ID {}: {}", idUsuario, ex.getMessage());
                          return Mono.empty();
                        })
                    .onErrorResume(
                        Exception.class,
                        ex -> {
                          log.error(
                              "Error genérico al obtener usuario con ID {}: {}",
                              idUsuario,
                              ex.getMessage());
                          return Mono.empty();
                        }));
  }

  @Override
  public Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad) {
    log.info("Obteniendo usuario por documento: {}", documentoIdentidad);

    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(auth -> auth != null ? (String) auth.getCredentials() : null)
        .defaultIfEmpty(usuariosToken)
        .flatMap(
            token ->
                webClient
                    .get()
                    .uri(usuariosBaseUrl + USUARIOS_BY_DOCUMENTO_ENDPOINT, documentoIdentidad)
                    .headers(
                        headers -> {
                          if (token != null && !token.isEmpty()) {
                            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
                          }
                        })
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .doOnNext(
                        usuario ->
                            log.debug(
                                "Usuario encontrado - ID: {}, Documento: {}",
                                usuario.getIdUsuario(),
                                usuario.getDocumentoIdentidad()))
                    .map(this::mapToUsuario)
                    .doOnSuccess(
                        usuario ->
                            log.info(
                                "Usuario con documento {} obtenido exitosamente",
                                documentoIdentidad))
                    .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> {
                          log.warn("Usuario con documento {} no encontrado", documentoIdentidad);
                          return Mono.empty();
                        })
                    .onErrorResume(
                        WebClientResponseException.class,
                        ex -> {
                          log.error(
                              "Error al obtener usuario con documento {}: {}",
                              documentoIdentidad,
                              ex.getMessage());
                          return Mono.empty();
                        })
                    .onErrorResume(
                        Exception.class,
                        ex -> {
                          log.error(
                              "Error genérico al obtener usuario con documento {}: {}",
                              documentoIdentidad,
                              ex.getMessage());
                          return Mono.empty();
                        }));
  }

  @Override
  public Mono<Boolean> existeUsuarioPorDocumento(String documentoIdentidad) {
    log.info("Validando existencia de usuario con documento: {}", documentoIdentidad);

    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(auth -> auth != null ? (String) auth.getCredentials() : null)
        .defaultIfEmpty(usuariosToken)
        .flatMap(
            token ->
                webClient
                    .get()
                    .uri(usuariosBaseUrl + USUARIOS_BY_DOCUMENTO_ENDPOINT, documentoIdentidad)
                    .headers(
                        headers -> {
                          if (token != null && !token.isEmpty()) {
                            headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
                          }
                        })
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .doOnNext(
                        usuario ->
                            log.debug(
                                "Usuario encontrado - ID: {}, Activo: {}",
                                usuario.getIdUsuario(),
                                usuario.isActivo()))
                    .map(this::esUsuarioValido)
                    .doOnSuccess(
                        existe ->
                            log.info(
                                "Usuario con documento {} existe y es válido: {}",
                                documentoIdentidad,
                                existe))
                    .onErrorResume(
                        WebClientResponseException.class,
                        ex -> exceptionHandler.handleWebClientException(ex, documentoIdentidad))
                    .onErrorResume(
                        Exception.class,
                        ex -> exceptionHandler.handleGenericException(ex, documentoIdentidad)));
  }

  private Usuario mapToUsuario(UsuarioResponseDTO dto) {
    return Usuario.builder()
        .idUsuario(dto.getIdUsuario())
        .nombre(dto.getNombre())
        .apellido(dto.getApellido())
        .email(dto.getEmail())
        .documentoIdentidad(dto.getDocumentoIdentidad())
        .telefono(dto.getTelefono())
        .idRol(dto.getIdRol())
        .rol(dto.getRol())
        .salarioBase(dto.getSalarioBase())
        .activo(dto.isActivo())
        .build();
  }

  private boolean esUsuarioValido(UsuarioResponseDTO usuario) {
    if (usuario == null) {
      log.warn("Usuario response es null");
      return false;
    }

    return true;
  }
}
