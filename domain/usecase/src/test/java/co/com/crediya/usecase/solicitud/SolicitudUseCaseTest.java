package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.exceptions.AlreadyExistException;
import co.com.crediya.usecase.solicitud.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        solicitudUseCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository);
    }

    @Test
    @DisplayName("Debería crear solicitud exitosamente cuando todos los datos son válidos")
    void deberiaCrearSolicitudExitosamenteCuandoTodosLosDatosSonValidos() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = new BigDecimal("1000000");
        LocalDate plazo = LocalDate.now().plusMonths(12);
        Long idTipoPrestamo = 1L;
        Long idEstadoPendiente = 1L;

        TipoPrestamo tipoPrestamo = new TipoPrestamo(
                idTipoPrestamo,
                "Préstamo Personal",
                new BigDecimal("500000"),
                new BigDecimal("5000000"),
                new BigDecimal("15.5"),
                true
        );

        Solicitud solicitudEsperada = Solicitud.toSolicitud(
                documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoPendiente
        );

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitudEsperada));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectNext(solicitudEsperada)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar AlreadyExistException cuando ya existe solicitud para el documento")
    void deberiaLanzarAlreadyExistExceptionCuandoYaExisteSolicitudParaElDocumento() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = new BigDecimal("1000000");
        LocalDate plazo = LocalDate.now().plusMonths(12);
        Long idTipoPrestamo = 1L;

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(true));
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectErrorMatches(throwable ->
                    throwable instanceof AlreadyExistException &&
                    throwable.getMessage().contains("Ya existe una solicitud para el documento de identidad: " + documentoIdentidad))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar ValidationException cuando idTipoPrestamo es nulo")
    void deberiaLanzarValidationExceptionCuandoIdTipoPrestamoEsNulo() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = new BigDecimal("1000000");
        LocalDate plazo = LocalDate.now().plusMonths(12);
        Long idTipoPrestamo = null;

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectErrorMatches(throwable ->
                    throwable instanceof ValidationException &&
                    throwable.getMessage().equals("El ID del tipo de préstamo es obligatorio"))
                .verify();
    }

    @Test
    @DisplayName("Debería crear solicitud con monto exacto al límite superior del tipo de préstamo")
    void deberiaCrearSolicitudConMontoExactoAlLimiteSuperiorDelTipoPrestamo() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = new BigDecimal("5000000"); // Exactamente el máximo
        LocalDate plazo = LocalDate.now().plusMonths(12);
        Long idTipoPrestamo = 1L;
        Long idEstadoPendiente = 1L;

        TipoPrestamo tipoPrestamo = new TipoPrestamo(
                idTipoPrestamo,
                "Préstamo Personal",
                new BigDecimal("500000"),
                new BigDecimal("5000000"), // Exactamente igual al monto
                new BigDecimal("15.5"),
                true
        );

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(Solicitud.toSolicitud(
                    documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoPendiente)));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería crear solicitud con monto exacto al límite inferior del tipo de préstamo")
    void deberiaCrearSolicitudConMontoExactoAlLimiteInferiorDelTipoPrestamo() {
        // Arrange
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        BigDecimal monto = new BigDecimal("500000"); // Exactamente el mínimo
        LocalDate plazo = LocalDate.now().plusMonths(12);
        Long idTipoPrestamo = 1L;
        Long idEstadoPendiente = 1L;

        TipoPrestamo tipoPrestamo = new TipoPrestamo(
                idTipoPrestamo,
                "Préstamo Personal",
                new BigDecimal("500000"), // Exactamente igual al monto
                new BigDecimal("5000000"),
                new BigDecimal("15.5"),
                true
        );

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(Solicitud.toSolicitud(
                    documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoPendiente)));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería validar el flujo completo de validaciones en orden correcto")
    void deberiaValidarElFlujoCompletoDeValidacionesEnOrdenCorrecto() {
        // Arrange
        String documentoIdentidad = "87654321";
        String email = "otro@example.com";
        BigDecimal monto = new BigDecimal("2000000");
        LocalDate plazo = LocalDate.now().plusMonths(24);
        Long idTipoPrestamo = 2L;
        Long idEstadoPendiente = 2L;

        TipoPrestamo tipoPrestamo = new TipoPrestamo(
                idTipoPrestamo,
                "Préstamo Hipotecario",
                new BigDecimal("1000000"),
                new BigDecimal("10000000"),
                new BigDecimal("12.0"),
                false
        );

        Solicitud solicitudEsperada = Solicitud.toSolicitud(
                documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstadoPendiente
        );

        when(solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitudEsperada));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo))
                .expectNext(solicitudEsperada)
                .verifyComplete();
    }
}
