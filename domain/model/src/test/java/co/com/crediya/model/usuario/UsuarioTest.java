package co.com.crediya.model.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Debe crear usuario correctamente usando builder con todos los campos")
    void debeCrearUsuarioCorrectamenteUsandoBuilder() {
        // Arrange
        Long idUsuario = 1L;
        String nombre = "Juan";
        String apellido = "Pérez";
        String email = "juan.perez@example.com";
        String documentoIdentidad = "12345678";
        String telefono = "555-1234";
        Long idRol = 2L;
        boolean activo = true;

        // Act
        Usuario usuario = Usuario.builder()
                .idUsuario(idUsuario)
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .documentoIdentidad(documentoIdentidad)
                .telefono(telefono)
                .idRol(idRol)
                .activo(activo)
                .build();

        // Assert
        assertAll(
            () -> assertEquals(idUsuario, usuario.getIdUsuario()),
            () -> assertEquals(nombre, usuario.getNombre()),
            () -> assertEquals(apellido, usuario.getApellido()),
            () -> assertEquals(email, usuario.getEmail()),
            () -> assertEquals(documentoIdentidad, usuario.getDocumentoIdentidad()),
            () -> assertEquals(telefono, usuario.getTelefono()),
            () -> assertEquals(idRol, usuario.getIdRol()),
            () -> assertTrue(usuario.isActivo())
        );
    }

    @Test
    @DisplayName("Debe crear usuario con campos mínimos usando builder")
    void debeCrearUsuarioConCamposMínimos() {
        // Arrange
        String nombre = "María";
        String email = "maria@example.com";

        // Act
        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .build();

        // Assert
        assertAll(
            () -> assertNull(usuario.getIdUsuario()),
            () -> assertEquals(nombre, usuario.getNombre()),
            () -> assertNull(usuario.getApellido()),
            () -> assertEquals(email, usuario.getEmail()),
            () -> assertNull(usuario.getDocumentoIdentidad()),
            () -> assertNull(usuario.getTelefono()),
            () -> assertNull(usuario.getIdRol()),
            () -> assertFalse(usuario.isActivo()) // default value for boolean
        );
    }

    @Test
    @DisplayName("Debe permitir modificar campos usando setters de Lombok")
    void debePermitirModificarCamposUsandoSetters() {
        // Arrange
        Usuario usuario = Usuario.builder()
                .nombre("Pedro")
                .email("pedro@example.com")
                .activo(false)
                .build();

        // Act
        usuario.setApellido("González");
        usuario.setTelefono("555-9876");
        usuario.setActivo(true);

        // Assert
        assertAll(
            () -> assertEquals("Pedro", usuario.getNombre()),
            () -> assertEquals("González", usuario.getApellido()),
            () -> assertEquals("pedro@example.com", usuario.getEmail()),
            () -> assertEquals("555-9876", usuario.getTelefono()),
            () -> assertTrue(usuario.isActivo())
        );
    }

    @Test
    @DisplayName("Debe implementar equals y hashCode correctamente por Lombok")
    void debeImplementarEqualsYHashCodeCorrectamente() {
        // Arrange
        Usuario usuario1 = Usuario.builder()
                .idUsuario(1L)
                .nombre("Ana")
                .apellido("López")
                .email("ana.lopez@example.com")
                .documentoIdentidad("87654321")
                .telefono("555-5555")
                .idRol(1L)
                .activo(true)
                .build();

        Usuario usuario2 = Usuario.builder()
                .idUsuario(1L)
                .nombre("Ana")
                .apellido("López")
                .email("ana.lopez@example.com")
                .documentoIdentidad("87654321")
                .telefono("555-5555")
                .idRol(1L)
                .activo(true)
                .build();

        Usuario usuario3 = Usuario.builder()
                .idUsuario(2L)
                .nombre("Ana")
                .apellido("López")
                .email("ana.lopez@example.com")
                .documentoIdentidad("87654321")
                .telefono("555-5555")
                .idRol(1L)
                .activo(true)
                .build();

        // Act & Assert
        assertAll(
            () -> assertEquals(usuario1, usuario2),
            () -> assertEquals(usuario1.hashCode(), usuario2.hashCode()),
            () -> assertNotEquals(usuario1, usuario3),
            () -> assertNotEquals(usuario1.hashCode(), usuario3.hashCode())
        );
    }

    @Test
    @DisplayName("Debe implementar toString correctamente por Lombok")
    void debeImplementarToStringCorrectamente() {
        // Arrange
        Usuario usuario = Usuario.builder()
                .idUsuario(1L)
                .nombre("Carlos")
                .apellido("Rodríguez")
                .email("carlos.rodriguez@example.com")
                .documentoIdentidad("11223344")
                .telefono("555-7777")
                .idRol(3L)
                .activo(true)
                .build();

        // Act
        String toStringResult = usuario.toString();

        // Assert
        assertAll(
            () -> assertNotNull(toStringResult),
            () -> assertTrue(toStringResult.contains("Carlos")),
            () -> assertTrue(toStringResult.contains("Rodríguez")),
            () -> assertTrue(toStringResult.contains("carlos.rodriguez@example.com")),
            () -> assertTrue(toStringResult.contains("11223344")),
            () -> assertTrue(toStringResult.contains("Usuario"))
        );
    }

    @Test
    @DisplayName("Debe crear usuario inactivo por defecto")
    void debeCrearUsuarioInactivoPorDefecto() {
        // Arrange & Act
        Usuario usuario = Usuario.builder()
                .nombre("Lucia")
                .email("lucia@example.com")
                .build();

        // Assert
        assertFalse(usuario.isActivo());
    }

    @Test
    @DisplayName("Debe permitir crear usuario con valores nulos")
    void debePermitirCrearUsuarioConValoresNulos() {
        // Arrange & Act
        Usuario usuario = Usuario.builder()
                .idUsuario(null)
                .nombre(null)
                .apellido(null)
                .email(null)
                .documentoIdentidad(null)
                .telefono(null)
                .idRol(null)
                .activo(false)
                .build();

        // Assert
        assertAll(
            () -> assertNull(usuario.getIdUsuario()),
            () -> assertNull(usuario.getNombre()),
            () -> assertNull(usuario.getApellido()),
            () -> assertNull(usuario.getEmail()),
            () -> assertNull(usuario.getDocumentoIdentidad()),
            () -> assertNull(usuario.getTelefono()),
            () -> assertNull(usuario.getIdRol()),
            () -> assertFalse(usuario.isActivo())
        );
    }
}
