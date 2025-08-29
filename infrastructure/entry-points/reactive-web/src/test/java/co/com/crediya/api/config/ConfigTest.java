package co.com.crediya.api.config;

import co.com.crediya.api.UsuarioHandler;
import co.com.crediya.api.UsuarioRouterRest;
import co.com.crediya.api.mapper.UsuarioDTOMapper;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

//@ContextConfiguration(classes = {UsuarioRouterRest.class, UsuarioHandler.class, ConfigTest.TestConfig.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {
/*
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().is5xxServerError() // Esperamos 500 porque el handler arroja IllegalArgumentException
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    static class TestConfig {

        @Bean
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }

        @Bean
        public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository) {
            return new UsuarioUseCase(usuarioRepository);
        }

        @Bean
        public UsuarioDTOMapper usuarioDTOMapper() {
            return Mockito.mock(UsuarioDTOMapper.class);
        }

        @Bean
        public Validator validator() {
            return Mockito.mock(Validator.class);
        }
    }
    */
}