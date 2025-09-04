package co.com.crediya.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

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
}
