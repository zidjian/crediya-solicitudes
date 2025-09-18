package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.CapacidadEndeudamientoGateway;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.NotificationGateway;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
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
import java.util.List;

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

    @Mock
    private NotificationGateway notificationGateway;

    @Mock
    private CapacidadEndeudamientoGateway capacidadEndeudamientoGateway;

    @Mock
    private UsuarioRepository usuarioRepository;

    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        solicitudUseCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository, notificationGateway, capacidadEndeudamientoGateway, usuarioRepository);
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitudEsperada));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
                .thenReturn(Mono.just(true));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
                .thenReturn(Mono.just(false));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
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

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
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
        when(usuarioRepository.obtenerUsuarioPorDocumento(documentoIdentidad))
                .thenReturn(Mono.just(co.com.crediya.model.usuario.Usuario.builder()
                        .idUsuario(1L)
                        .nombre("Test")
                        .apellido("User")
                        .email("test@example.com")
                        .documentoIdentidad(documentoIdentidad)
                        .telefono("123456789")
                        .idRol(1L)
                        .rol("administrador")
                        .salarioBase(new BigDecimal("10000"))
                        .activo(true)
                        .build()));
        when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(co.com.crediya.model.usuario.Usuario.class), any(Solicitud.class), any(List.class), any(Long.class)))
                .thenReturn(Mono.just("messageId"));
        when(usuarioRepository.obtenerUsuarioPorDocumento(documentoIdentidad))
                .thenReturn(Mono.just(co.com.crediya.model.usuario.Usuario.builder()
                        .idUsuario(1L)
                        .nombre("Test")
                        .apellido("User")
                        .email("test@example.com")
                        .documentoIdentidad(documentoIdentidad)
                        .telefono("123456789")
                        .idRol(1L)
                        .activo(true)
                        .build()));
        when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(co.com.crediya.model.usuario.Usuario.class), any(Solicitud.class), any(List.class), any(Long.class)))
                .thenReturn(Mono.just("messageId"));
        when(usuarioRepository.obtenerUsuarioPorId(1L))
                .thenReturn(Mono.just(co.com.crediya.model.usuario.Usuario.builder()
                        .idUsuario(1L)
                        .nombre("Test")
                        .apellido("User")
                        .email("test@example.com")
                        .documentoIdentidad(documentoIdentidad)
                        .telefono("123456789")
                        .idRol(1L)
                        .rol("administrador")
                        .salarioBase(new BigDecimal("10000"))
                        .activo(true)
                        .build()));
        when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(co.com.crediya.model.usuario.Usuario.class), any(Solicitud.class), any(List.class), any(Long.class)))
                .thenReturn(Mono.just("messageId"));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
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

        when(solicitudRepository.existePorIdUser(documentoIdentidad))
                .thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.existeById(idTipoPrestamo))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.obtenerIdEstadoPendienteRevision())
                .thenReturn(Mono.just(idEstadoPendiente));
        when(solicitudRepository.crear(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitudEsperada));
        when(usuarioRepository.obtenerUsuarioPorId(1L))
                .thenReturn(Mono.just(co.com.crediya.model.usuario.Usuario.builder()
                        .idUsuario(1L)
                        .nombre("Test")
                        .apellido("User")
                        .email("test@example.com")
                        .documentoIdentidad(documentoIdentidad)
                        .telefono("123456789")
                        .idRol(1L)
                        .rol("administrador")
                        .salarioBase(new BigDecimal("10000"))
                        .activo(true)
                        .build()));
        when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(co.com.crediya.model.usuario.Usuario.class), any(Solicitud.class), any(List.class), any(Long.class)))
                .thenReturn(Mono.just("messageId"));
        when(usuarioRepository.obtenerUsuarioPorId(1L))
                .thenReturn(Mono.just(co.com.crediya.model.usuario.Usuario.builder()
                        .idUsuario(1L)
                        .nombre("Test")
                        .apellido("User")
                        .email("test@example.com")
                        .documentoIdentidad(documentoIdentidad)
                        .telefono("123456789")
                        .idRol(1L)
                        .activo(true)
                        .build()));
        when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(co.com.crediya.model.usuario.Usuario.class), any(Solicitud.class), any(List.class), any(Long.class)))
                .thenReturn(Mono.just("messageId"));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.crearSolicitud(documentoIdentidad, email, monto, plazo, idTipoPrestamo);

        // Assert
        StepVerifier.create(resultado)
                .expectNext(solicitudEsperada)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si idSolicitud es nulo al actualizar")
    void deberiaLanzarValidationExceptionSiIdSolicitudEsNuloAlActualizar() {
        // Arrange
        Long idSolicitud = null;
        Long idEstado = 1L;

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(idSolicitud, idEstado);

        // Assert
        StepVerifier.create(resultado)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El ID de la solicitud es obligatorio"))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el estado no existe al actualizar")
    void deberiaLanzarValidationExceptionSiEstadoNoExisteAlActualizar() {
        // Arrange
        when(solicitudRepository.existeById(1L)).thenReturn(Mono.just(true));
        when(estadoRepository.findById(2L)).thenReturn(Mono.empty());
        when(solicitudRepository.findById(1L)).thenReturn(Mono.just(Solicitud.toSolicitud("doc", "mail", BigDecimal.ONE, LocalDate.now(), 1L, 1L)));

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, 2L);

        // Assert
        StepVerifier.create(resultado)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().contains("No existe un estado con ID: 2"))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si nuevoIdEstado es nulo al actualizar")
    void deberiaLanzarValidationExceptionSiNuevoIdEstadoEsNuloAlActualizar() {
        // Arrange
        Long nuevoIdEstadoNulo = null;

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, nuevoIdEstadoNulo);

        // Assert
        StepVerifier.create(resultado)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El ID del estado es obligatorio"))
                .verify();
    }

    @Test
    @DisplayName("Debería actualizar solicitud y enviar notificación correctamente")
    void deberiaActualizarSolicitudYNotificarCorrectamente() {
        // Arrange
        Solicitud solicitud = Solicitud.toSolicitud("doc", "mail@test.com", BigDecimal.ONE, LocalDate.now(), 1L, 1L);
        Solicitud solicitudActualizada = solicitud.cambiarEstado(2L);
        when(solicitudRepository.existeById(1L)).thenReturn(Mono.just(true));
        when(estadoRepository.findById(2L)).thenReturn(Mono.just(new co.com.crediya.model.solicitud.Estado(2L, "Aprobado", "desc")));
        when(solicitudRepository.findById(1L)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.actualizar(any(Solicitud.class))).thenReturn(Mono.just(solicitudActualizada));
        when(notificationGateway.enviarNotificacionEstadoSolicitud("mail@test.com", "Aprobado", solicitudActualizada.getIdSolicitud())).thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, 2L);

        // Assert
        StepVerifier.create(resultado)
                .expectNext(solicitudActualizada)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería obtener solicitudes paginadas correctamente")
    void deberiaObtenerSolicitudesPaginadasCorrectamente() {
        // Arrange
        int page = 0;
        int size = 10;
        List<Solicitud> solicitudes = List.of(
            Solicitud.toSolicitud("doc1", "mail1@test.com", BigDecimal.valueOf(1000), LocalDate.now(), 1L, 1L),
            Solicitud.toSolicitud("doc2", "mail2@test.com", BigDecimal.valueOf(2000), LocalDate.now(), 1L, 1L)
        );
        PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudes, page, size, 2);

        when(solicitudRepository.obtenerSolicitudes(page, size)).thenReturn(Mono.just(expectedPageResult));

        // Act
        Mono<PageResult<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

        // Assert
        StepVerifier.create(resultado)
                .expectNext(expectedPageResult)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería obtener solicitudes paginadas con página diferente a cero")
    void deberiaObtenerSolicitudesPaginadasConPaginaDiferenteACero() {
        // Arrange
        int page = 1;
        int size = 5;
        List<Solicitud> solicitudes = List.of(
            Solicitud.toSolicitud("doc3", "mail3@test.com", BigDecimal.valueOf(3000), LocalDate.now(), 1L, 1L)
        );
        PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudes, page, size, 6);

        when(solicitudRepository.obtenerSolicitudes(page, size)).thenReturn(Mono.just(expectedPageResult));

        // Act
        Mono<PageResult<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

        // Assert
        StepVerifier.create(resultado)
                .expectNext(expectedPageResult)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería obtener solicitudes paginadas con lista vacía")
    void deberiaObtenerSolicitudesPaginadasConListaVacia() {
        // Arrange
        int page = 0;
        int size = 10;
        List<Solicitud> solicitudesVacias = List.of();
        PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudesVacias, page, size, 0);

        when(solicitudRepository.obtenerSolicitudes(page, size)).thenReturn(Mono.just(expectedPageResult));

        // Act
        Mono<PageResult<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

        // Assert
        StepVerifier.create(resultado)
                .expectNext(expectedPageResult)
                .verifyComplete();
    }
}
