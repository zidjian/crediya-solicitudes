package co.com.crediya.usecase.solicitud.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AlreadyExistExceptionTest {

    @Test
    void debeCrearExcepcionConMensajeCodigoYStatusCorrectos() {
        // Arrange
        String mensajeEsperado = "El recurso ya existe";
        String codigoEsperado = "ALREADY_EXIST";
        int statusEsperado = 409;

        // Act
        AlreadyExistException excepcion = new AlreadyExistException(mensajeEsperado);

        // Assert
        assertEquals(mensajeEsperado, excepcion.getMessage());
        assertEquals(codigoEsperado, excepcion.getCode());
        assertEquals(statusEsperado, excepcion.getHttpStatus());
    }
}