package co.com.crediya.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TipoPrestamo Domain Model Tests")
class TipoPrestamoTest {

  @Test
  @DisplayName("toTipoPrestamo - Debe crear tipo préstamo exitosamente con datos válidos")
  void toTipoPrestamo_ConDatosValidos_DebeCrearTipoPrestamoExitosamente() {
    // Arrange
    Long idTipoPrestamo = 1L;
    String nombre = "Préstamo Personal";
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);
    boolean validacionAutomatica = true;

    // Act
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            idTipoPrestamo, nombre, montoMinimo, montoMaximo, tasaInteres, validacionAutomatica);

    // Assert
    assertNotNull(tipoPrestamo);
    assertEquals(idTipoPrestamo, tipoPrestamo.getIdTipoPrestamo());
    assertEquals(nombre, tipoPrestamo.getNombre());
    assertEquals(montoMinimo, tipoPrestamo.getMontoMinimo());
    assertEquals(montoMaximo, tipoPrestamo.getMontoMaximo());
    assertEquals(tasaInteres, tipoPrestamo.getTasaInteres());
    assertTrue(tipoPrestamo.isValidacionAutomatica());
  }

  @Test
  @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando montoMinimo es nulo")
  void constructor_CuandoMontoMinimoEsNulo_DebeLanzarIllegalArgumentException() {
    // Arrange
    BigDecimal montoMinimoNulo = null;
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimoNulo, montoMaximo, tasaInteres, true));

    assertEquals("El monto mínimo debe ser mayor a cero", exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1000", "-0.01"})
  @DisplayName(
      "constructor - Debe lanzar IllegalArgumentException cuando montoMinimo es menor o igual a cero")
  void constructor_CuandoMontoMinimoEsMenorOIgualACero_DebeLanzarIllegalArgumentException(
      String montoStr) {
    // Arrange
    BigDecimal montoMinimo = new BigDecimal(montoStr);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimo, montoMaximo, tasaInteres, true));

    assertEquals("El monto mínimo debe ser mayor a cero", exception.getMessage());
  }

  @Test
  @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando montoMaximo es nulo")
  void constructor_CuandoMontoMaximoEsNulo_DebeLanzarIllegalArgumentException() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);
    BigDecimal montoMaximoNulo = null;
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimo, montoMaximoNulo, tasaInteres, true));

    assertEquals("El monto máximo debe ser mayor o igual al monto mínimo", exception.getMessage());
  }

  @Test
  @DisplayName(
      "constructor - Debe lanzar IllegalArgumentException cuando montoMaximo es menor al mínimo")
  void constructor_CuandoMontoMaximoEsMenorAlMinimo_DebeLanzarIllegalArgumentException() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(10000);
    BigDecimal montoMaximo = BigDecimal.valueOf(5000);
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimo, montoMaximo, tasaInteres, true));

    assertEquals("El monto máximo debe ser mayor o igual al monto mínimo", exception.getMessage());
  }

  @Test
  @DisplayName("constructor - Debe aceptar montoMaximo igual al mínimo")
  void constructor_CuandoMontoMaximoEsIgualAlMinimo_DebeCrearTipoPrestamoExitosamente() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(10000);
    BigDecimal montoMaximo = BigDecimal.valueOf(10000);
    BigDecimal tasaInteres = BigDecimal.valueOf(0.15);

    // Act
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(1L, "Préstamo", montoMinimo, montoMaximo, tasaInteres, true);

    // Assert
    assertNotNull(tipoPrestamo);
    assertEquals(montoMinimo, tipoPrestamo.getMontoMinimo());
    assertEquals(montoMaximo, tipoPrestamo.getMontoMaximo());
  }

  @Test
  @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando tasaInteres es nula")
  void constructor_CuandoTasaInteresEsNula_DebeLanzarIllegalArgumentException() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteresNula = null;

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimo, montoMaximo, tasaInteresNula, true));

    assertEquals("La tasa de interés no puede ser negativa", exception.getMessage());
  }

  @Test
  @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando tasaInteres es negativa")
  void constructor_CuandoTasaInteresEsNegativa_DebeLanzarIllegalArgumentException() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteresNegativa = BigDecimal.valueOf(-0.05);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                TipoPrestamo.toTipoPrestamo(
                    1L, "Préstamo", montoMinimo, montoMaximo, tasaInteresNegativa, true));

    assertEquals("La tasa de interés no puede ser negativa", exception.getMessage());
  }

  @Test
  @DisplayName("constructor - Debe aceptar tasaInteres cero")
  void constructor_CuandoTasaInteresEsCero_DebeCrearTipoPrestamoExitosamente() {
    // Arrange
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);
    BigDecimal tasaInteresCero = BigDecimal.ZERO;

    // Act
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L, "Préstamo", montoMinimo, montoMaximo, tasaInteresCero, true);

    // Assert
    assertNotNull(tipoPrestamo);
    assertEquals(tasaInteresCero, tipoPrestamo.getTasaInteres());
  }

  @Test
  @DisplayName("validarMonto - Debe retornar true para monto dentro del rango")
  void validarMonto_ConMontoEnRango_DebeRetornarTrue() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoValido = BigDecimal.valueOf(25000);

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoValido);

    // Assert
    assertTrue(resultado);
  }

  @Test
  @DisplayName("validarMonto - Debe retornar true para monto en límite inferior")
  void validarMonto_ConMontoEnLimiteInferior_DebeRetornarTrue() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoMinimo = BigDecimal.valueOf(1000);

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoMinimo);

    // Assert
    assertTrue(resultado);
  }

  @Test
  @DisplayName("validarMonto - Debe retornar true para monto en límite superior")
  void validarMonto_ConMontoEnLimiteSuperior_DebeRetornarTrue() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoMaximo = BigDecimal.valueOf(50000);

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoMaximo);

    // Assert
    assertTrue(resultado);
  }

  @Test
  @DisplayName("validarMonto - Debe retornar false para monto menor al mínimo")
  void validarMonto_ConMontoMenorAlMinimo_DebeRetornarFalse() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoMenor = BigDecimal.valueOf(999);

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoMenor);

    // Assert
    assertFalse(resultado);
  }

  @Test
  @DisplayName("validarMonto - Debe retornar false para monto mayor al máximo")
  void validarMonto_ConMontoMayorAlMaximo_DebeRetornarFalse() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoMayor = BigDecimal.valueOf(50001);

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoMayor);

    // Assert
    assertFalse(resultado);
  }

  @Test
  @DisplayName("validarMonto - Debe retornar false para monto nulo")
  void validarMonto_ConMontoNulo_DebeRetornarFalse() {
    // Arrange
    TipoPrestamo tipoPrestamo =
        TipoPrestamo.toTipoPrestamo(
            1L,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true);
    BigDecimal montoNulo = null;

    // Act
    boolean resultado = tipoPrestamo.validarMonto(montoNulo);

    // Assert
    assertFalse(resultado);
  }
}
