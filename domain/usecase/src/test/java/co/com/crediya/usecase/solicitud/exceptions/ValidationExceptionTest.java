package co.com.crediya.usecase.solicitud.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ValidationExceptionTest {

    @Test
    void debeCrearExcepcionDeValidacionConMensaje() {
        // Arrange
        String mensajeEsperado = "Error de validación";
        String codigoEsperado = "VALIDATION_ERROR";
        int statusEsperado = 400;

        // Act
        ValidationException excepcion = new ValidationException(mensajeEsperado);

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
    }

    @Test
    void debeCrearExcepcionDeValidacionConMensajeYCausa() {
        // Arrange
        String mensajeEsperado = "Error de validación con causa";
        Throwable causa = new RuntimeException("Causa interna");
        String codigoEsperado = "VALIDATION_ERROR";
        int statusEsperado = 400;

        // Act
        ValidationException excepcion = new ValidationException(mensajeEsperado, causa);

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
        assertEquals(causa, excepcion.getCause());
    }
}