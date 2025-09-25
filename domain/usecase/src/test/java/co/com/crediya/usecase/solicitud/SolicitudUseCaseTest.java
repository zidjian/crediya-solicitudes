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
import co.com.crediya.usecase.solicitud.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SolicitudUseCaseTest {

  @Mock private SolicitudRepository solicitudRepository;

  @Mock private TipoPrestamoRepository tipoPrestamoRepository;

  @Mock private EstadoRepository estadoRepository;

  @Mock private NotificationGateway notificationGateway;

  @Mock private CapacidadEndeudamientoGateway capacidadEndeudamientoGateway;

  @Mock private UsuarioRepository usuarioRepository;

  private SolicitudUseCase solicitudUseCase;

  @BeforeEach
  void setUp() {
    solicitudUseCase =
        new SolicitudUseCase(
            solicitudRepository,
            tipoPrestamoRepository,
            estadoRepository,
            notificationGateway,
            capacidadEndeudamientoGateway,
            usuarioRepository);
  }

  @Test
  @DisplayName("Debería crear solicitud exitosamente cuando todos los datos son válidos")
  void deberiaCrearSolicitudExitosamenteCuandoTodosLosDatosSonValidos() {
    // Arrange
    String idUser = "12345678";
    String email = "test@example.com";
    BigDecimal monto = new BigDecimal("1000000");
    LocalDate plazo = LocalDate.now().plusMonths(12);
    Long idTipoPrestamo = 1L;
    Long idEstadoPendiente = 1L;

    TipoPrestamo tipoPrestamo =
        new TipoPrestamo(
            idTipoPrestamo,
            "Préstamo Personal",
            new BigDecimal("500000"),
            new BigDecimal("5000000"),
            new BigDecimal("15.5"),
            true);

    Solicitud solicitudEsperada =
        Solicitud.toSolicitud(
            idUser, email, monto, plazo, idTipoPrestamo, idEstadoPendiente);

    when(tipoPrestamoRepository.existeById(idTipoPrestamo)).thenReturn(Mono.just(true));
    when(tipoPrestamoRepository.findById(idTipoPrestamo)).thenReturn(Mono.just(tipoPrestamo));
    when(estadoRepository.obtenerIdEstadoPendienteRevision())
        .thenReturn(Mono.just(idEstadoPendiente));
    when(solicitudRepository.crear(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
    
    // Mock for automatic validation process since validacionAutomatica = true
    when(usuarioRepository.obtenerUsuarioPorId(any(Long.class))).thenReturn(Mono.empty());
    when(solicitudRepository.obtenerSolicitudesPorIdUser(any(String.class))).thenReturn(Mono.just(List.of()));
    when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(), any(), any(), any())).thenReturn(Mono.empty());

    // Act
    Mono<Solicitud> resultado =
        solicitudUseCase.crearSolicitud(idUser, email, monto, plazo, idTipoPrestamo);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudEsperada).verifyComplete();
  }


  @Test
  @DisplayName("Debería lanzar ValidationException cuando idTipoPrestamo es nulo")
  void deberiaLanzarValidationExceptionCuandoIdTipoPrestamoEsNulo() {
    // Arrange
    String idUser = "12345678";
    String email = "test@example.com";
    BigDecimal monto = new BigDecimal("1000000");
    LocalDate plazo = LocalDate.now().plusMonths(12);
    Long idTipoPrestamo = null;

    // Act
    Mono<Solicitud> resultado =
        solicitudUseCase.crearSolicitud(idUser, email, monto, plazo, idTipoPrestamo);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().equals("El ID del tipo de préstamo es obligatorio"))
        .verify();
  }

  @Test
  @DisplayName("Debería crear solicitud con monto exacto al límite superior del tipo de préstamo")
  void deberiaCrearSolicitudConMontoExactoAlLimiteSuperiorDelTipoPrestamo() {
    // Arrange
    String idUser = "12345678";
    String email = "test@example.com";
    BigDecimal monto = new BigDecimal("5000000"); // Exactamente el máximo
    LocalDate plazo = LocalDate.now().plusMonths(12);
    Long idTipoPrestamo = 1L;
    Long idEstadoPendiente = 1L;

    TipoPrestamo tipoPrestamo =
        new TipoPrestamo(
            idTipoPrestamo,
            "Préstamo Personal",
            new BigDecimal("500000"),
            new BigDecimal("5000000"), // Exactamente igual al monto
            new BigDecimal("15.5"),
            true);

    Solicitud solicitudEsperada = Solicitud.toSolicitud(idUser, email, monto, plazo, idTipoPrestamo, idEstadoPendiente);

    when(tipoPrestamoRepository.existeById(idTipoPrestamo)).thenReturn(Mono.just(true));
    when(tipoPrestamoRepository.findById(idTipoPrestamo)).thenReturn(Mono.just(tipoPrestamo));
    when(estadoRepository.obtenerIdEstadoPendienteRevision())
        .thenReturn(Mono.just(idEstadoPendiente));
    when(solicitudRepository.crear(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
    
    // Mock for automatic validation process since validacionAutomatica = true
    when(usuarioRepository.obtenerUsuarioPorId(any(Long.class))).thenReturn(Mono.empty());
    when(solicitudRepository.obtenerSolicitudesPorIdUser(any(String.class))).thenReturn(Mono.just(List.of()));
    when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(), any(), any(), any())).thenReturn(Mono.empty());

    // Act
    Mono<Solicitud> resultado =
        solicitudUseCase.crearSolicitud(idUser, email, monto, plazo, idTipoPrestamo);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudEsperada).verifyComplete();
  }

  @Test
  @DisplayName("Debería crear solicitud con monto exacto al límite inferior del tipo de préstamo")
  void deberiaCrearSolicitudConMontoExactoAlLimiteInferiorDelTipoPrestamo() {
    // Arrange
    String idUser = "12345678";
    String email = "test@example.com";
    BigDecimal monto = new BigDecimal("500000"); // Exactamente el mínimo
    LocalDate plazo = LocalDate.now().plusMonths(12);
    Long idTipoPrestamo = 1L;
    Long idEstadoPendiente = 1L;

    TipoPrestamo tipoPrestamo =
        new TipoPrestamo(
            idTipoPrestamo,
            "Préstamo Personal",
            new BigDecimal("500000"), // Exactamente igual al monto
            new BigDecimal("5000000"),
            new BigDecimal("15.5"),
            true);

    Solicitud solicitudEsperada = Solicitud.toSolicitud(idUser, email, monto, plazo, idTipoPrestamo, idEstadoPendiente);

    when(tipoPrestamoRepository.existeById(idTipoPrestamo)).thenReturn(Mono.just(true));
    when(tipoPrestamoRepository.findById(idTipoPrestamo)).thenReturn(Mono.just(tipoPrestamo));
    when(estadoRepository.obtenerIdEstadoPendienteRevision())
        .thenReturn(Mono.just(idEstadoPendiente));
    when(solicitudRepository.crear(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
    
    // Mock for automatic validation process since validacionAutomatica = true
    when(usuarioRepository.obtenerUsuarioPorId(any(Long.class))).thenReturn(Mono.empty());
    when(solicitudRepository.obtenerSolicitudesPorIdUser(any(String.class))).thenReturn(Mono.just(List.of()));
    when(capacidadEndeudamientoGateway.enviarSolicitudCapacidadEndeudamiento(any(), any(), any(), any())).thenReturn(Mono.empty());

    // Act
    Mono<Solicitud> resultado =
        solicitudUseCase.crearSolicitud(idUser, email, monto, plazo, idTipoPrestamo);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudEsperada).verifyComplete();
  }

  @Test
  @DisplayName("Debería validar el flujo completo de validaciones en orden correcto")
  void deberiaValidarElFlujoCompletoDeValidacionesEnOrdenCorrecto() {
    // Arrange
    String idUser = "87654321";
    String email = "otro@example.com";
    BigDecimal monto = new BigDecimal("2000000");
    LocalDate plazo = LocalDate.now().plusMonths(24);
    Long idTipoPrestamo = 2L;
    Long idEstadoPendiente = 2L;

    TipoPrestamo tipoPrestamo =
        new TipoPrestamo(
            idTipoPrestamo,
            "Préstamo Hipotecario",
            new BigDecimal("1000000"),
            new BigDecimal("10000000"),
            new BigDecimal("12.0"),
            false);

    Solicitud solicitudEsperada =
        Solicitud.toSolicitud(
            idUser, email, monto, plazo, idTipoPrestamo, idEstadoPendiente);

    when(tipoPrestamoRepository.existeById(idTipoPrestamo)).thenReturn(Mono.just(true));
    when(tipoPrestamoRepository.findById(idTipoPrestamo)).thenReturn(Mono.just(tipoPrestamo));
    when(estadoRepository.obtenerIdEstadoPendienteRevision())
        .thenReturn(Mono.just(idEstadoPendiente));
    when(solicitudRepository.crear(any(Solicitud.class))).thenReturn(Mono.just(solicitudEsperada));
    
    // Since validacionAutomatica = false, no additional mocks needed for automatic validation

    // Act
    Mono<Solicitud> resultado =
        solicitudUseCase.crearSolicitud(idUser, email, monto, plazo, idTipoPrestamo);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudEsperada).verifyComplete();
  }

  @Test
  @DisplayName("Debería lanzar ValidationException si idSolicitud es nulo al actualizar")
  void deberiaLanzarValidationExceptionSiIdSolicitudEsNuloAlActualizar() {
    // Arrange
    Long idSolicitud = null;
    Long idEstado = 1L;

    // Act
    Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(idSolicitud, idEstado, true);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().equals("El ID de la solicitud es obligatorio"))
        .verify();
  }

  @Test
  @DisplayName("Debería lanzar ValidationException si el estado no existe al actualizar")
  void deberiaLanzarValidationExceptionSiEstadoNoExisteAlActualizar() {
    // Arrange
    when(solicitudRepository.existeById(1L)).thenReturn(Mono.just(true));
    when(estadoRepository.findById(2L)).thenReturn(Mono.empty());
    when(solicitudRepository.findById(1L))
        .thenReturn(
            Mono.just(
                Solicitud.toSolicitud("doc", "mail", BigDecimal.ONE, LocalDate.now(), 1L, 1L)));

    // Act
    Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, 2L, true);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().contains("No existe un estado con ID: 2"))
        .verify();
  }

  @Test
  @DisplayName("Debería lanzar ValidationException si nuevoIdEstado es nulo al actualizar")
  void deberiaLanzarValidationExceptionSiNuevoIdEstadoEsNuloAlActualizar() {
    // Arrange
    Long nuevoIdEstadoNulo = null;

    // Act
    Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, nuevoIdEstadoNulo, true);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().equals("El ID del estado es obligatorio"))
        .verify();
  }

  @Test
  @DisplayName("Debería actualizar solicitud y enviar notificación correctamente")
  void deberiaActualizarSolicitudYNotificarCorrectamente() {
    // Arrange
    Solicitud solicitud =
        Solicitud.toSolicitud("doc", "mail@test.com", BigDecimal.ONE, LocalDate.now(), 1L, 1L);
    Solicitud solicitudActualizada = solicitud.cambiarEstado(2L);
    when(solicitudRepository.existeById(1L)).thenReturn(Mono.just(true));
    when(estadoRepository.findById(2L))
        .thenReturn(Mono.just(new co.com.crediya.model.solicitud.Estado(2L, "Aprobado", "desc")));
    when(solicitudRepository.findById(1L)).thenReturn(Mono.just(solicitud));
    when(solicitudRepository.actualizar(any(Solicitud.class)))
        .thenReturn(Mono.just(solicitudActualizada));
    when(notificationGateway.enviarNotificacionEstadoSolicitud(
            "mail@test.com", "Aprobado", solicitudActualizada.getIdSolicitud()))
        .thenReturn(Mono.empty());

    // Act
    Mono<Solicitud> resultado = solicitudUseCase.actualizarSolicitud(1L, 2L, true);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudActualizada).verifyComplete();
  }

  @Test
  @DisplayName("Debería obtener solicitudes paginadas correctamente")
  void deberiaObtenerSolicitudesPaginadasCorrectamente() {
    // Arrange
    int page = 0;
    int size = 10;
    List<Solicitud> solicitudes =
        List.of(
            Solicitud.toSolicitud(
                "doc1", "mail1@test.com", BigDecimal.valueOf(1000), LocalDate.now(), 1L, 1L),
            Solicitud.toSolicitud(
                "doc2", "mail2@test.com", BigDecimal.valueOf(2000), LocalDate.now(), 1L, 1L));
    PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudes, page, size, 2);

    when(solicitudRepository.obtenerSolicitudes(page, size))
        .thenReturn(Mono.just(expectedPageResult));

    // Act
    Mono<PageResult<Solicitud>> resultado =
        solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

    // Assert
    StepVerifier.create(resultado).expectNext(expectedPageResult).verifyComplete();
  }

  @Test
  @DisplayName("Debería obtener solicitudes paginadas con página diferente a cero")
  void deberiaObtenerSolicitudesPaginadasConPaginaDiferenteACero() {
    // Arrange
    int page = 1;
    int size = 5;
    List<Solicitud> solicitudes =
        List.of(
            Solicitud.toSolicitud(
                "doc3", "mail3@test.com", BigDecimal.valueOf(3000), LocalDate.now(), 1L, 1L));
    PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudes, page, size, 6);

    when(solicitudRepository.obtenerSolicitudes(page, size))
        .thenReturn(Mono.just(expectedPageResult));

    // Act
    Mono<PageResult<Solicitud>> resultado =
        solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

    // Assert
    StepVerifier.create(resultado).expectNext(expectedPageResult).verifyComplete();
  }

  @Test
  @DisplayName("Debería obtener solicitudes paginadas con lista vacía")
  void deberiaObtenerSolicitudesPaginadasConListaVacia() {
    // Arrange
    int page = 0;
    int size = 10;
    List<Solicitud> solicitudesVacias = List.of();
    PageResult<Solicitud> expectedPageResult = new PageResult<>(solicitudesVacias, page, size, 0);

    when(solicitudRepository.obtenerSolicitudes(page, size))
        .thenReturn(Mono.just(expectedPageResult));

    // Act
    Mono<PageResult<Solicitud>> resultado =
        solicitudUseCase.obtenerSolicitudesPaginadas(page, size);

    // Assert
    StepVerifier.create(resultado).expectNext(expectedPageResult).verifyComplete();
  }

  @Test
  @DisplayName("Debería lanzar ValidationException cuando idUsuario es nulo en obtener solicitudes por idUsuario")
  void deberiaLanzarValidationExceptionCuandoIdUsuarioEsNuloEnObtenerSolicitudesPorIdUsuario() {
    // Arrange
    String idUsuario = null;

    // Act
    Mono<List<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPorIdUsuario(idUsuario);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().equals("El ID del usuario es obligatorio"))
        .verify();
  }

  @Test
  @DisplayName("Debería lanzar ValidationException cuando idUsuario está vacío en obtener solicitudes por idUsuario")
  void deberiaLanzarValidationExceptionCuandoIdUsuarioEstaVacioEnObtenerSolicitudesPorIdUsuario() {
    // Arrange
    String idUsuario = "";

    // Act
    Mono<List<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPorIdUsuario(idUsuario);

    // Assert
    StepVerifier.create(resultado)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ValidationException
                    && throwable.getMessage().equals("El ID del usuario es obligatorio"))
        .verify();
  }

  @Test
  @DisplayName("Debería obtener solicitudes por idUsuario exitosamente")
  void deberiaObtenerSolicitudesPorIdUsuarioExitosamente() {
    // Arrange
    String idUsuario = "12345678";
    List<Solicitud> solicitudesEsperadas = List.of(
        Solicitud.toSolicitud("12345678", "test@example.com", BigDecimal.valueOf(1000), LocalDate.now(), 1L, 1L)
    );

    when(solicitudRepository.obtenerSolicitudesPorIdUser(idUsuario)).thenReturn(Mono.just(solicitudesEsperadas));

    // Act
    Mono<List<Solicitud>> resultado = solicitudUseCase.obtenerSolicitudesPorIdUsuario(idUsuario);

    // Assert
    StepVerifier.create(resultado).expectNext(solicitudesEsperadas).verifyComplete();
  }

  private boolean solicitudMatches(Solicitud actual, Solicitud expected) {
    return actual.getIdUser().equals(expected.getIdUser()) &&
           actual.getEmail().equals(expected.getEmail()) &&
           actual.getMonto().compareTo(expected.getMonto()) == 0 &&
           actual.getPlazo().equals(expected.getPlazo()) &&
           actual.getIdTipoPrestamo().equals(expected.getIdTipoPrestamo());
  }
}
