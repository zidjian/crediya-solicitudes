package co.com.crediya.usecase.solicitud.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

  static class BusinessExceptionDummy extends BusinessException {
    public BusinessExceptionDummy(String code, String message, int httpStatus) {
      super(code, message, httpStatus);
    }
  }

  @Test
  void debeCrearBusinessExceptionConParametrosCorrectos() {
    // Arrange
    String codigoEsperado = "CODIGO_PRUEBA";
    String mensajeEsperado = "Mensaje de prueba";
    int statusEsperado = 400;

    // Act
    BusinessException excepcion =
        new BusinessExceptionDummy(codigoEsperado, mensajeEsperado, statusEsperado);

    // Assert
    assertEquals(codigoEsperado, excepcion.getCode());
    assertEquals(mensajeEsperado, excepcion.getMessage());
    assertEquals(statusEsperado, excepcion.getHttpStatus());
  }
}
