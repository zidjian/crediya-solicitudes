package co.com.crediya.model.usuario.gateways;

import co.com.crediya.model.usuario.Usuario;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioRepositoryTest {

  @Test
  void obtenerUsuarioPorId_deberiaRetornarUsuario_whenExiste() {
    // Arrange
    UsuarioRepository repo = mock(UsuarioRepository.class);
    Usuario usuario =
        Usuario.builder()
            .idUsuario(1L)
            .nombre("Juan")
            .apellido("Perez")
            .documentoIdentidad("12345")
            .email("juan@example.com")
            .telefono("3001234567")
            .idRol(2L)
            .activo(true)
            .build();
    when(repo.obtenerUsuarioPorId(1L)).thenReturn(Mono.just(usuario));

    // Act
    Usuario resultado = repo.obtenerUsuarioPorId(1L).block();

    // Assert
    assertNotNull(resultado);
    assertEquals(1L, resultado.getIdUsuario());
    assertEquals("Juan", resultado.getNombre());
    assertEquals("12345", resultado.getDocumentoIdentidad());
    verify(repo, times(1)).obtenerUsuarioPorId(1L);
  }

  @Test
  void obtenerUsuarioPorDocumento_deberiaRetornarVacio_whenNoExiste() {
    // Arrange
    UsuarioRepository repo = mock(UsuarioRepository.class);
    when(repo.obtenerUsuarioPorDocumento("no-existe")).thenReturn(Mono.empty());

    // Act
    Usuario resultado = repo.obtenerUsuarioPorDocumento("no-existe").block();

    // Assert
    assertNull(resultado);
    verify(repo, times(1)).obtenerUsuarioPorDocumento("no-existe");
  }

  @Test
  void obtenerUsuarioPorDocumento_deberiaRetornarUsuario_whenExiste() {
    // Arrange
    UsuarioRepository repo = mock(UsuarioRepository.class);
    Usuario usuario =
        Usuario.builder()
            .idUsuario(2L)
            .nombre("María")
            .apellido("González")
            .documentoIdentidad("87654321")
            .email("maria@example.com")
            .telefono("555-9876")
            .idRol(2L)
            .activo(true)
            .build();
    when(repo.obtenerUsuarioPorDocumento("87654321")).thenReturn(Mono.just(usuario));

    // Act
    Usuario resultado = repo.obtenerUsuarioPorDocumento("87654321").block();

    // Assert
    assertNotNull(resultado);
    assertEquals(2L, resultado.getIdUsuario());
    assertEquals("María", resultado.getNombre());
    assertEquals("87654321", resultado.getDocumentoIdentidad());
    verify(repo, times(1)).obtenerUsuarioPorDocumento("87654321");
  }
}
