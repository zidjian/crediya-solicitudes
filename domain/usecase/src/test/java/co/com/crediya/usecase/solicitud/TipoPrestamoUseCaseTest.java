package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TipoPrestamoUseCase Tests")
class TipoPrestamoUseCaseTest {

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @InjectMocks
    private TipoPrestamoUseCase tipoPrestamoUseCase;

    private TipoPrestamo tipoPrestamoMock;
    private Long validIdTipoPrestamo;

    @BeforeEach
    void setUp() {
        validIdTipoPrestamo = 1L;
        tipoPrestamoMock = TipoPrestamo.toTipoPrestamo(
            validIdTipoPrestamo,
            "Préstamo Personal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(50000),
            BigDecimal.valueOf(0.15),
            true
        );
    }

    @Test
    @DisplayName("findById - Debe retornar tipo de préstamo cuando existe")
    void findById_CuandoTipoPrestamoExiste_DebeRetornarTipoPrestamo() {
        // Arrange
        when(tipoPrestamoRepository.findById(validIdTipoPrestamo)).thenReturn(Mono.just(tipoPrestamoMock));

        // Act & Assert
        StepVerifier.create(tipoPrestamoUseCase.findById(validIdTipoPrestamo))
                .expectNext(tipoPrestamoMock)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - Debe lanzar IllegalArgumentException cuando id es nulo")
    void findById_CuandoIdEsNulo_DebeLanzarIllegalArgumentException() {
        // Arrange
        Long idNulo = null;

        // Act & Assert
        StepVerifier.create(tipoPrestamoUseCase.findById(idNulo))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("findById - Debe lanzar NotFoundException cuando tipo de préstamo no existe")
    void findById_CuandoTipoPrestamoNoExiste_DebeLanzarNotFoundException() {
        // Arrange
        when(tipoPrestamoRepository.findById(validIdTipoPrestamo)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(tipoPrestamoUseCase.findById(validIdTipoPrestamo))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("No existe un tipo de préstamo con id: " + validIdTipoPrestamo))
                .verify();
    }

    @Test
    @DisplayName("findById - Debe propagar NotFoundException sin modificar")
    void findById_CuandoOcurreNotFoundException_DebePropagarSinModificar() {
        // Arrange
        NotFoundException notFoundException = new NotFoundException("Tipo de préstamo no encontrado");
        when(tipoPrestamoRepository.findById(validIdTipoPrestamo)).thenReturn(Mono.error(notFoundException));

        // Act & Assert
        StepVerifier.create(tipoPrestamoUseCase.findById(validIdTipoPrestamo))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("findById - Debe envolver otros errores en RuntimeException")
    void findById_CuandoOcurreOtroError_DebeEnvolverEnRuntimeException() {
        // Arrange
        Exception otroError = new RuntimeException("Error de conexión");
        when(tipoPrestamoRepository.findById(validIdTipoPrestamo)).thenReturn(Mono.error(otroError));

        // Act & Assert
        StepVerifier.create(tipoPrestamoUseCase.findById(validIdTipoPrestamo))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error al buscar el tipo de préstamo con id: " + validIdTipoPrestamo) &&
                        throwable.getCause() == otroError)
                .verify();
    }
}
