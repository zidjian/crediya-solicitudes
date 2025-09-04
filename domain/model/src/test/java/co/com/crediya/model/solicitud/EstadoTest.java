package co.com.crediya.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Estado Domain Model Tests")
class EstadoTest {

    @Test
    @DisplayName("toEstado - Debe crear estado exitosamente con datos válidos")
    void toEstado_ConDatosValidos_DebeCrearEstadoExitosamente() {
        // Arrange
        Long idEstado = 1L;
        String nombre = "PENDIENTE";
        String descripcion = "Estado pendiente de revisión";

        // Act
        Estado estado = Estado.toEstado(idEstado, nombre, descripcion);

        // Assert
        assertNotNull(estado);
        assertEquals(idEstado, estado.getIdEstado());
        assertEquals(nombre, estado.getNombre());
        assertEquals(descripcion, estado.getDescripcion());
    }

    @Test
    @DisplayName("constructor - Debe lanzar IllegalArgumentException cuando nombre es nulo")
    void constructor_CuandoNombreEsNulo_DebeLanzarIllegalArgumentException() {
        // Arrange
        Long idEstado = 1L;
        String nombreNulo = null;
        String descripcion = "Estado de prueba";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Estado.toEstado(idEstado, nombreNulo, descripcion)
        );

        assertEquals("El nombre del estado es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("constructor - Debe crear estado con descripción nula")
    void constructor_ConDescripcionNula_DebeCrearEstadoExitosamente() {
        // Arrange
        Long idEstado = 1L;
        String nombre = "APROBADO";
        String descripcionNula = null;

        // Act
        Estado estado = Estado.toEstado(idEstado, nombre, descripcionNula);

        // Assert
        assertNotNull(estado);
        assertEquals(idEstado, estado.getIdEstado());
        assertEquals(nombre, estado.getNombre());
        assertNull(estado.getDescripcion());
    }

    @Test
    @DisplayName("constructor - Debe crear estado con id nulo")
    void constructor_ConIdNulo_DebeCrearEstadoExitosamente() {
        // Arrange
        Long idNulo = null;
        String nombre = "RECHAZADO";
        String descripcion = "Estado rechazado";

        // Act
        Estado estado = Estado.toEstado(idNulo, nombre, descripcion);

        // Assert
        assertNotNull(estado);
        assertNull(estado.getIdEstado());
        assertEquals(nombre, estado.getNombre());
        assertEquals(descripcion, estado.getDescripcion());
    }

    @Test
    @DisplayName("getters - Deben retornar valores correctos")
    void getters_DebenRetornarValoresCorrectos() {
        // Arrange
        Long expectedId = 5L;
        String expectedNombre = "EN_EVALUACION";
        String expectedDescripcion = "Solicitud en proceso de evaluación";
        Estado estado = Estado.toEstado(expectedId, expectedNombre, expectedDescripcion);

        // Act & Assert
        assertEquals(expectedId, estado.getIdEstado());
        assertEquals(expectedNombre, estado.getNombre());
        assertEquals(expectedDescripcion, estado.getDescripcion());
    }
}
