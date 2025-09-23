package co.com.crediya.model.usuario.gateways;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioValidacionRepositoryTest {

  @Test
  void existeUsuarioPorDocumento_deberiaRetornarTrue_whenExiste() {
    // Arrange
    UsuarioValidacionRepository repo = mock(UsuarioValidacionRepository.class);
    when(repo.existeUsuarioPorDocumento("12345")).thenReturn(Mono.just(Boolean.TRUE));

    // Act
    Boolean resultado = repo.existeUsuarioPorDocumento("12345").block();

    // Assert
    assertNotNull(resultado);
    assertTrue(resultado);
    verify(repo, times(1)).existeUsuarioPorDocumento("12345");
  }

  @Test
  void existeUsuarioPorDocumento_deberiaRetornarFalse_whenNoExiste() {
    // Arrange
    UsuarioValidacionRepository repo = mock(UsuarioValidacionRepository.class);
    when(repo.existeUsuarioPorDocumento("no-existe")).thenReturn(Mono.just(Boolean.FALSE));

    // Act
    Boolean resultado = repo.existeUsuarioPorDocumento("no-existe").block();

    // Assert
    assertNotNull(resultado);
    assertFalse(resultado);
    verify(repo, times(1)).existeUsuarioPorDocumento("no-existe");
  }
}
