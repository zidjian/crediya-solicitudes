package co.com.crediya.httpclient;

import co.com.crediya.httpclient.dto.UsuarioResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class UsuarioHttpClientTest {

    private MockWebServer mockWebServer;
    private UsuarioHttpClient usuarioHttpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.builder().build();

        usuarioHttpClient = new UsuarioHttpClient(webClient);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void debeRetornarTrueCuandoUsuarioExisteYEstaActivo() throws JsonProcessingException {
        // Given
        String documento = "12345678";
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(documento, "Juan Perez", "juan@test.com", true);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(usuario))
                .addHeader("Content-Type", "application/json"));

        // When & Then
        StepVerifier.create(usuarioHttpClient.existeUsuarioPorDocumento(documento))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void debeRetornarFalseCuandoUsuarioNoExiste() {
        // Given
        String documento = "87654321";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        // When & Then
        StepVerifier.create(usuarioHttpClient.existeUsuarioPorDocumento(documento))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void debeRetornarFalseCuandoUsuarioExistePeroNoEstaActivo() throws JsonProcessingException {
        // Given
        String documento = "11111111";
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(documento, "Ana Lopez", "ana@test.com", false);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(usuario))
                .addHeader("Content-Type", "application/json"));

        // When & Then
        StepVerifier.create(usuarioHttpClient.existeUsuarioPorDocumento(documento))
                .expectNext(false)
                .verifyComplete();
    }
}
