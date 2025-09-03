package co.com.crediya.api.config;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        // Test basic CORS configuration
        webTestClient.options()
                .uri("/api/v1/test")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .exchange()
                .expectStatus().isOk();
    }
}