package co.com.crediya.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Solicitud Domain Model Tests")
class SolicitudTest {

    @Test
    @DisplayName("toSolicitud - Debe crear solicitud exitosamente con datos válidos")
    void toSolicitud_ConDatosValidos_DebeCrearSolicitudExitosamente() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act
        Solicitud solicitud = Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstado);

        // Assert
        assertNotNull(solicitud);
        assertNull(solicitud.getIdSolicitud()); // Se crea sin ID hasta persistir
        assertEquals(documentoIdentidad, solicitud.getDocumentoIdentidad());
        assertEquals(email, solicitud.getEmail());
        assertEquals(monto, solicitud.getMonto());
        assertEquals(plazo, solicitud.getPlazo());
        assertEquals(idTipoPrestamo, solicitud.getIdTipoPrestamo());
        assertEquals(idEstado, solicitud.getIdEstado());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando documentoIdentidad es nulo o vacío")
    void constructor_CuandoDocumentoIdentidadEsNuloOVacio_DebeLanzarIllegalArgumentException(String documentoInvalido) {
        // Arrange
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoInvalido, email, monto, plazo, idTipoPrestamo, idEstado)
        );

        assertEquals("El documento de identidad es obligatorio", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando email es nulo o vacío")
    void constructor_CuandoEmailEsNuloOVacio_DebeLanzarIllegalArgumentException(String emailInvalido) {
        // Arrange
        String documentoIdentidad = "12345678";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoIdentidad, emailInvalido, monto, plazo, idTipoPrestamo, idEstado)
        );

        assertEquals("El email es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando monto es nulo")
    void constructor_CuandoMontoEsNulo_DebeLanzarIllegalArgumentException() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal montoNulo = null;
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoIdentidad, email, montoNulo, plazo, idTipoPrestamo, idEstado)
        );

        assertEquals("El monto debe ser mayor a cero", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1000", "-0.01"})
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando monto es menor o igual a cero")
    void constructor_CuandoMontoEsMenorOIgualACero_DebeLanzarIllegalArgumentException(String montoStr) {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal montoInvalido = new BigDecimal(montoStr);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoIdentidad, email, montoInvalido, plazo, idTipoPrestamo, idEstado)
        );

        assertEquals("El monto debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando plazo es nulo")
    void constructor_CuandoPlazoEsNulo_DebeLanzarIllegalArgumentException() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazoNulo = null;
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoIdentidad, email, monto, plazoNulo, idTipoPrestamo, idEstado)
        );

        assertEquals("El plazo es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando idTipoPrestamo es nulo")
    void constructor_CuandoIdTipoPrestamoEsNulo_DebeLanzarIllegalArgumentException() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamoNulo = null;
        Long idEstado = 2L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamoNulo, idEstado)
        );

        assertEquals("El ID del tipo de préstamo es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("constructor - Debe permitir idEstado nulo")
    void constructor_ConIdEstadoNulo_DebeCrearSolicitudExitosamente() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstadoNulo = null;

        // Act
        Solicitud solicitud = Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoNulo);

        // Assert
        assertNotNull(solicitud);
        assertNull(solicitud.getIdEstado());
    }

    @Test
    @DisplayName("constructor - Debe trimear documento de identidad y email")
    void constructor_ConEspaciosEnDocumentoYEmail_DebeTrimerarValores() {
        // Arrange
        String documentoConEspacios = "  12345678  ";
        String emailConEspacios = "  test@example.com  ";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act
        Solicitud solicitud = Solicitud.toSolicitud(documentoConEspacios, emailConEspacios, monto, plazo, idTipoPrestamo, idEstado);

        // Assert
        assertEquals("12345678", solicitud.getDocumentoIdentidad());
        assertEquals("test@example.com", solicitud.getEmail());
    }

    @Test
    @DisplayName("cambiarEstado - Debe crear nueva instancia con estado cambiado")
    void cambiarEstado_ConNuevoEstado_DebeCrearNuevaInstanciaConEstadoCambiado() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstadoInicial = 1L;
        Long nuevoIdEstado = 2L;

        Solicitud solicitudOriginal = Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoInicial);

        // Act
        Solicitud solicitudModificada = solicitudOriginal.cambiarEstado(nuevoIdEstado);

        // Assert
        assertNotNull(solicitudModificada);
        assertNotSame(solicitudOriginal, solicitudModificada); // Debe ser una nueva instancia
        assertEquals(nuevoIdEstado, solicitudModificada.getIdEstado());
        assertEquals(solicitudOriginal.getDocumentoIdentidad(), solicitudModificada.getDocumentoIdentidad());
        assertEquals(solicitudOriginal.getEmail(), solicitudModificada.getEmail());
        assertEquals(solicitudOriginal.getMonto(), solicitudModificada.getMonto());
        assertEquals(solicitudOriginal.getPlazo(), solicitudModificada.getPlazo());
        assertEquals(solicitudOriginal.getIdTipoPrestamo(), solicitudModificada.getIdTipoPrestamo());

        // El estado original no debe cambiar
        assertEquals(idEstadoInicial, solicitudOriginal.getIdEstado());
    }

    @Test
    @DisplayName("getters - Deben retornar valores correctos")
    void getters_DebenRetornarValoresCorrectos() {
        // Arrange
        String expectedDocumento = "87654321";
        String expectedEmail = "usuario@test.com";
        BigDecimal expectedMonto = BigDecimal.valueOf(25000);
        LocalDate expectedPlazo = LocalDate.of(2025, 12, 31);
        Long expectedIdTipoPrestamo = 3L;
        Long expectedIdEstado = 1L;

        Solicitud solicitud = Solicitud.toSolicitud(
            expectedDocumento, expectedEmail, expectedMonto, expectedPlazo, expectedIdTipoPrestamo, expectedIdEstado
        );

        // Act & Assert
        assertEquals(expectedDocumento, solicitud.getDocumentoIdentidad());
        assertEquals(expectedEmail, solicitud.getEmail());
        assertEquals(expectedMonto, solicitud.getMonto());
        assertEquals(expectedPlazo, solicitud.getPlazo());
        assertEquals(expectedIdTipoPrestamo, solicitud.getIdTipoPrestamo());
        assertEquals(expectedIdEstado, solicitud.getIdEstado());
        assertNull(solicitud.getIdSolicitud()); // Debería ser nulo para solicitudes nuevas
    }

    @Test
    @DisplayName("toSolicitud - Debe crear solicitud con valores extremos válidos")
    void toSolicitud_ConValoresExtremosValidos_DebeCrearSolicitudExitosamente() {
        // Arrange
        String documentoIdentidad = "1"; // Mínimo válido
        String email = "a@b.c"; // Mínimo válido
        BigDecimal monto = new BigDecimal("0.01"); // Mínimo positivo
        LocalDate plazo = LocalDate.MIN; // Fecha mínima
        Long idTipoPrestamo = 1L;
        Long idEstado = 1L;

        // Act
        Solicitud solicitud = Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstado);

        // Assert
        assertNotNull(solicitud);
        assertEquals(documentoIdentidad, solicitud.getDocumentoIdentidad());
        assertEquals(email, solicitud.getEmail());
        assertEquals(monto, solicitud.getMonto());
        assertEquals(plazo, solicitud.getPlazo());
        assertEquals(idTipoPrestamo, solicitud.getIdTipoPrestamo());
        assertEquals(idEstado, solicitud.getIdEstado());
    }

    @Test
    @DisplayName("fromDatabase - Debe crear solicitud desde base de datos con ID")
    void fromDatabase_ConDatosValidos_DebeCrearSolicitudExitosamente() {
        // Arrange
        Long idSolicitud = 123L;
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = BigDecimal.valueOf(10000);
        LocalDate plazo = LocalDate.now().plusYears(1);
        Long idTipoPrestamo = 1L;
        Long idEstado = 2L;

        // Act
        Solicitud solicitud = Solicitud.fromDatabase(idSolicitud, documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstado);

        // Assert
        assertNotNull(solicitud);
        assertEquals(idSolicitud, solicitud.getIdSolicitud());
        assertEquals(documentoIdentidad, solicitud.getDocumentoIdentidad());
        assertEquals(email, solicitud.getEmail());
        assertEquals(monto, solicitud.getMonto());
        assertEquals(plazo, solicitud.getPlazo());
        assertEquals(idTipoPrestamo, solicitud.getIdTipoPrestamo());
        assertEquals(idEstado, solicitud.getIdEstado());
    }

    @Test
    @DisplayName("calcularDeudaTotalMensual - Debe calcular correctamente con tasa de interés positiva")
    void calcularDeudaTotalMensual_ConTasaInteresPositiva_DebeCalcularCorrectamente() {
        // Arrange
        Solicitud solicitud = Solicitud.toSolicitud("123", "test@test.com", BigDecimal.valueOf(12000), LocalDate.now().plusMonths(12), 1L, 1L);
        BigDecimal tasaInteresAnual = BigDecimal.valueOf(12.0); // 12%

        // Act
        BigDecimal resultado = solicitud.calcularDeudaTotalMensual(tasaInteresAnual);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
        // El resultado debería ser mayor al monto mensual sin intereses (12000/12 = 1000)
        assertTrue(resultado.compareTo(BigDecimal.valueOf(1000)) > 0);
    }

    @Test
    @DisplayName("calcularDeudaTotalMensual - Debe calcular correctamente con tasa de interés cero")
    void calcularDeudaTotalMensual_ConTasaInteresCero_DebeCalcularCorrectamente() {
        // Arrange
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaPlazo = fechaActual.plusMonths(12);
        Solicitud solicitud = Solicitud.toSolicitud("123", "test@test.com", BigDecimal.valueOf(12000), fechaPlazo, 1L, 1L);
        BigDecimal tasaInteresAnual = BigDecimal.ZERO;

        // Act
        BigDecimal resultado = solicitud.calcularDeudaTotalMensual(tasaInteresAnual);

        // Assert
        assertNotNull(resultado);
        long meses = ChronoUnit.MONTHS.between(fechaActual, fechaPlazo);
        if (meses <= 0) meses = 1;
        BigDecimal expected = BigDecimal.valueOf(12000).divide(BigDecimal.valueOf(meses), 2, RoundingMode.HALF_UP);
        assertEquals(expected, resultado);
    }

    @Test
    @DisplayName("calcularDeudaTotalMensual - Debe manejar plazo en el pasado (meses <= 0)")
    void calcularDeudaTotalMensual_ConPlazoPasado_DebeUsarUnMes() {
        // Arrange
        LocalDate fechaPasada = LocalDate.now().minusMonths(2);
        Solicitud solicitud = Solicitud.toSolicitud("123", "test@test.com", BigDecimal.valueOf(12000), fechaPasada, 1L, 1L);
        BigDecimal tasaInteresAnual = BigDecimal.valueOf(12.0);

        // Act
        BigDecimal resultado = solicitud.calcularDeudaTotalMensual(tasaInteresAnual);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
        // For past dates, it should use 1 month, so result should be close to the full amount
        assertTrue(resultado.compareTo(BigDecimal.valueOf(10000)) >= 0);
    }

    @Test
    @DisplayName("calcularDeudaTotalMensual - Debe manejar plazo muy cercano (menos de un mes)")
    void calcularDeudaTotalMensual_ConPlazoMuyCercano_DebeUsarUnMes() {
        // Arrange
        LocalDate fechaCercana = LocalDate.now().plusDays(10);
        Solicitud solicitud = Solicitud.toSolicitud("123", "test@test.com", BigDecimal.valueOf(12000), fechaCercana, 1L, 1L);
        BigDecimal tasaInteresAnual = BigDecimal.valueOf(12.0);

        // Act
        BigDecimal resultado = solicitud.calcularDeudaTotalMensual(tasaInteresAnual);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
        // For very close dates (0 months), it should use 1 month, so result should be close to the full amount
        assertTrue(resultado.compareTo(BigDecimal.valueOf(10000)) >= 0);
    }

    @Test
    @DisplayName("calcularDeudaTotalMensual - Debe calcular correctamente con plazo de 6 meses")
    void calcularDeudaTotalMensual_ConPlazo6Meses_DebeCalcularCorrectamente() {
        // Arrange
        Solicitud solicitud = Solicitud.toSolicitud("123", "test@test.com", BigDecimal.valueOf(6000), LocalDate.now().plusMonths(6), 1L, 1L);
        BigDecimal tasaInteresAnual = BigDecimal.valueOf(10.0);

        // Act
        BigDecimal resultado = solicitud.calcularDeudaTotalMensual(tasaInteresAnual);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.valueOf(1000)) > 0); // Mayor a 6000/6 = 1000
        assertTrue(resultado.compareTo(BigDecimal.valueOf(1100)) < 0); // Menor a un valor razonable
    }
}
