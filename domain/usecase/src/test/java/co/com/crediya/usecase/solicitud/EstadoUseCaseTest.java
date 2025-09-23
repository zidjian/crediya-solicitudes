package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Estado;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.usecase.solicitud.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoUseCaseTest {

  @Mock private EstadoRepository estadoRepository;

  private EstadoUseCase estadoUseCase;

  @BeforeEach
  void setUp() {
    estadoUseCase = new EstadoUseCase(estadoRepository);
  }

  @Test
  @DisplayName("Debería obtener estado exitosamente cuando existe")
  void deberiaObtenerEstadoExitosamenteCuandoExiste() {
    // Arrange
    Long idEstado = 1L;
    Estado estadoEsperado = Estado.toEstado(idEstado, "Pendiente", "Estado pendiente de revisión");

    when(estadoRepository.findById(idEstado)).thenReturn(Mono.just(estadoEsperado));

    // Act & Assert
    StepVerifier.create(estadoUseCase.findById(idEstado))
        .expectNext(estadoEsperado)
        .verifyComplete();
  }

  @Test
  @DisplayName("Debería lanzar IllegalArgumentException cuando el ID es nulo")
  void deberiaLanzarIllegalArgumentExceptionCuandoIdEsNulo() {
    // Arrange
    Long idEstado = null;

    // Act & Assert
    StepVerifier.create(estadoUseCase.findById(idEstado))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  @DisplayName("Debería lanzar IllegalStateException cuando el estado no existe")
  void deberiaLanzarIllegalStateExceptionCuandoEstadoNoExiste() {
    // Arrange
    Long idEstado = 999L;

    when(estadoRepository.findById(idEstado)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(estadoUseCase.findById(idEstado))
        .expectError(IllegalStateException.class)
        .verify();
  }

  @Test
  @DisplayName("Debería preservar NotFoundException cuando viene del repositorio")
  void deberiaPreservarNotFoundExceptionCuandoVieneDelRepositorio() {
    // Arrange
    Long idEstado = 1L;
    NotFoundException notFoundException = new NotFoundException("Estado no encontrado");

    when(estadoRepository.findById(idEstado)).thenReturn(Mono.error(notFoundException));

    // Act & Assert
    StepVerifier.create(estadoUseCase.findById(idEstado))
        .expectError(NotFoundException.class)
        .verify();
  }

  @Test
  @DisplayName("Debería mapear otras excepciones a IllegalStateException")
  void deberiaMappearOtrasExcepcionesAIllegalStateException() {
    // Arrange
    Long idEstado = 1L;
    RuntimeException runtimeException = new RuntimeException("Error de base de datos");

    when(estadoRepository.findById(idEstado)).thenReturn(Mono.error(runtimeException));

    // Act & Assert
    StepVerifier.create(estadoUseCase.findById(idEstado))
        .expectErrorMatches(
            throwable ->
                throwable instanceof IllegalStateException
                    && throwable
                        .getMessage()
                        .contains("Error al obtener el estado con id: " + idEstado))
        .verify();
  }
}
