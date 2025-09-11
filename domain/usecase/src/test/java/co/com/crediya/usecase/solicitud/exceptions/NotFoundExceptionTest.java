package co.com.crediya.usecase.solicitud.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

    @Test
    void debeCrearExcepcionConValoresPorDefecto() {
        // Arrange
        String mensajeEsperado = "No encontrado";
        String codigoEsperado = "NOT_FOUND";
        int statusEsperado = 404;

        // Act
        NotFoundException excepcion = new NotFoundException();

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
    }

    @Test
    void debeCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajeEsperado = "Recurso no encontrado";
        String codigoEsperado = "NOT_FOUND";
        int statusEsperado = 404;

        // Act
        NotFoundException excepcion = new NotFoundException(mensajeEsperado);

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
    }

    @Test
    void debeCrearExcepcionConMensajeYThrowable() {
        // Arrange
        String mensajeEsperado = "Error con causa";
        Throwable causa = new RuntimeException("Causa interna");
        String codigoEsperado = "NOT_FOUND";
        int statusEsperado = 404;

        // Act
        NotFoundException excepcion = new NotFoundException(mensajeEsperado, causa);

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
        assertEquals(causa, excepcion.getCause());
    }
}