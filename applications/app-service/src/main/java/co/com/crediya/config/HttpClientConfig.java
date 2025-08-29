package co.com.crediya.config;

import co.com.crediya.model.usuario.gateways.UsuarioValidacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Configuration
public class HttpClientConfig {

    @Bean
    public UsuarioValidacionRepository usuarioValidacionRepository(
            WebClient webClient,
            @Value("${microservices.usuarios.base-url:http://localhost:8080}") String usuariosBaseUrl) {
        return new UsuarioHttpClientImpl(webClient, usuariosBaseUrl);
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class UsuarioHttpClientImpl implements UsuarioValidacionRepository {

        private final WebClient webClient;
        private final String usuariosBaseUrl;

        @Override
        public Mono<Boolean> existeUsuarioPorDocumento(String documentoIdentidad) {
            log.info("Validando existencia de usuario con documento: {}", documentoIdentidad);

            return webClient
                    .get()
                    .uri(usuariosBaseUrl + "/api/v1/usuarios/documento/{documento}", documentoIdentidad)
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .map(usuario -> usuario != null && usuario.getDocumentoIdentidad() != null)
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

    public static class UsuarioResponseDTO {
        private Long idUsuario;
        private String nombre;
        private String apellido;
        private String email;
        private String documentoIdentidad;
        private String telefono;
        private Long idRol;
        private Integer salarioBase;

        public UsuarioResponseDTO() {}

        public Long getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDocumentoIdentidad() { return documentoIdentidad; }
        public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public Long getIdRol() { return idRol; }
        public void setIdRol(Long idRol) { this.idRol = idRol; }

        public Integer getSalarioBase() { return salarioBase; }
        public void setSalarioBase(Integer salarioBase) { this.salarioBase = salarioBase; }
    }
}
