package co.com.crediya.usecase.solicitud.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TechnicalExceptionTest {

  @Test
  void debeCrearExcepcionTecnicaConMensaje() {
    // Arrange
    String mensajeEsperado = "Error técnico";
    String codigoEsperado = "INTERNAL_SERVER_ERROR";
    int statusEsperado = 500;

    // Act
    TechnicalException excepcion = new TechnicalException(mensajeEsperado);

    // Assert
    assertEquals(mensajeEsperado, excepcion.getMessage());
    assertEquals(codigoEsperado, excepcion.getCode());
    assertEquals(statusEsperado, excepcion.getHttpStatus());
  }

  @Test
  void debeCrearExcepcionTecnicaConMensajeYCausa() {
    // Arrange
    String mensajeEsperado = "Error técnico con causa";
    Throwable causa = new RuntimeException("Causa interna");
    String codigoEsperado = "INTERNAL_SERVER_ERROR";
    int statusEsperado = 500;

    // Act
    TechnicalException excepcion = new TechnicalException(mensajeEsperado, causa);

    // Assert
    assertEquals(mensajeEsperado, excepcion.getMessage());
    assertEquals(codigoEsperado, excepcion.getCode());
    assertEquals(statusEsperado, excepcion.getHttpStatus());
    assertEquals(causa, excepcion.getCause());
  }
}
