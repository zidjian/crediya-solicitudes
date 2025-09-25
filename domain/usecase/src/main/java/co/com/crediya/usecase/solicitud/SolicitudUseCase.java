package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.CapacidadEndeudamientoGateway;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.NotificationGateway;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.usecase.solicitud.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class SolicitudUseCase {
  private final SolicitudRepository solicitudRepository;
  private final TipoPrestamoRepository tipoPrestamoRepository;
  private final EstadoRepository estadoRepository;
  private final NotificationGateway notificationGateway;
  private final CapacidadEndeudamientoGateway capacidadEndeudamientoGateway;
  private final UsuarioRepository usuarioRepository;

  public Mono<Solicitud> crearSolicitud(
      String idUser, String email, BigDecimal monto, LocalDate plazo, Long idTipoPrestamo) {
    return validarIdTipoPrestamo(idTipoPrestamo)
        .flatMap(
            validatedIdTipoPrestamo ->
                validarTipoPrestamoExiste(validatedIdTipoPrestamo)
                    .then(validarMontoParaTipoPrestamo(monto, validatedIdTipoPrestamo))
                    .then(estadoRepository.obtenerIdEstadoPendienteRevision())
                    .map(
                        idEstadoPendiente ->
                            Solicitud.toSolicitud(
                                idUser,
                                email,
                                monto,
                                plazo,
                                validatedIdTipoPrestamo,
                                idEstadoPendiente)))
        .flatMap(solicitudRepository::crear)
        .flatMap(solicitud -> procesarValidacionAutomatica(solicitud, idTipoPrestamo));
  }

  public Mono<PageResult<Solicitud>> obtenerSolicitudesPaginadas(int page, int size) {
    return solicitudRepository.obtenerSolicitudes(page, size);
  }

  public Mono<List<Solicitud>> obtenerSolicitudesPorIdUsuario(String idUsuario) {
    return validarIdUsuario(idUsuario)
        .flatMap(
            validatedIdUsuario ->
                solicitudRepository.obtenerSolicitudesPorIdUser(validatedIdUsuario));
  }

  public Mono<Solicitud> actualizarSolicitud(Long idSolicitud, Long nuevoIdEstado, Boolean send) {
    return validarIdSolicitud(idSolicitud)
        .flatMap(
            validatedIdSolicitud ->
                validarIdEstado(nuevoIdEstado)
                    .flatMap(
                        validatedIdEstado ->
                            validarSolicitudExiste(validatedIdSolicitud)
                                .then(validarEstadoExiste(validatedIdEstado))
                                .then(solicitudRepository.findById(validatedIdSolicitud))
                                .map(solicitud -> solicitud.cambiarEstado(validatedIdEstado))
                                .flatMap(solicitudRepository::actualizar)
                                .flatMap(
                                    solicitudActualizada -> {
                                      if (Boolean.TRUE.equals(send)) {
                                        return estadoRepository
                                            .findById(validatedIdEstado)
                                            .flatMap(
                                                estado ->
                                                    notificationGateway
                                                        .enviarNotificacionEstadoSolicitud(
                                                            solicitudActualizada.getEmail(),
                                                            estado.getNombre(),
                                                            solicitudActualizada.getIdSolicitud()))
                                            .then(Mono.just(solicitudActualizada));
                                      } else {
                                        return Mono.just(solicitudActualizada);
                                      }
                                    })));
  }

  private Mono<Long> validarIdTipoPrestamo(Long idTipoPrestamo) {
    if (idTipoPrestamo == null) {
      return Mono.error(new ValidationException("El ID del tipo de préstamo es obligatorio"));
    }
    return Mono.just(idTipoPrestamo);
  }

  private Mono<Void> validarTipoPrestamoExiste(Long idTipoPrestamo) {
    return tipoPrestamoRepository
        .existeById(idTipoPrestamo)
        .flatMap(
            existe -> {
              if (Boolean.FALSE.equals(existe)) {
                return Mono.error(
                    new ValidationException(
                        String.format("No existe un tipo de préstamo con ID: %s", idTipoPrestamo)));
              }
              return Mono.empty();
            });
  }

  private Mono<Void> validarMontoParaTipoPrestamo(BigDecimal monto, Long idTipoPrestamo) {
    if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
      return Mono.error(new ValidationException("El monto debe ser mayor a cero"));
    }

    return tipoPrestamoRepository
        .findById(idTipoPrestamo)
        .flatMap(
            tipoPrestamo -> {
              if (!tipoPrestamo.validarMonto(monto)) {
                return Mono.error(
                    new ValidationException(
                        String.format(
                            "El monto %s no es válido para el tipo de préstamo. Debe estar entre %s y %s",
                            monto, tipoPrestamo.getMontoMinimo(), tipoPrestamo.getMontoMaximo())));
              }
              return Mono.empty();
            });
  }

  private Mono<Long> validarIdSolicitud(Long idSolicitud) {
    if (idSolicitud == null) {
      return Mono.error(new ValidationException("El ID de la solicitud es obligatorio"));
    }
    return Mono.just(idSolicitud);
  }

  private Mono<Void> validarSolicitudExiste(Long idSolicitud) {
    return solicitudRepository
        .existeById(idSolicitud)
        .flatMap(
            existe -> {
              if (Boolean.FALSE.equals(existe)) {
                return Mono.error(
                    new ValidationException(
                        String.format("No existe una solicitud con ID: %s", idSolicitud)));
              }
              return Mono.empty();
            });
  }

  private Mono<Long> validarIdEstado(Long idEstado) {
    if (idEstado == null) {
      return Mono.error(new ValidationException("El ID del estado es obligatorio"));
    }
    return Mono.just(idEstado);
  }

  private Mono<Void> validarEstadoExiste(Long idEstado) {
    return estadoRepository
        .findById(idEstado)
        .switchIfEmpty(
            Mono.error(
                new ValidationException(String.format("No existe un estado con ID: %s", idEstado))))
        .then();
  }

  private Mono<String> validarIdUsuario(String idUsuario) {
    if (idUsuario == null || idUsuario.trim().isEmpty()) {
      return Mono.error(new ValidationException("El ID del usuario es obligatorio"));
    }
    return Mono.just(idUsuario);
  }

  private Mono<Solicitud> procesarValidacionAutomatica(Solicitud solicitud, Long idTipoPrestamo) {
    return tipoPrestamoRepository
        .findById(idTipoPrestamo)
        .flatMap(
            tipoPrestamo -> {
              if (tipoPrestamo.isValidacionAutomatica()) {
                return usuarioRepository
                    .obtenerUsuarioPorId(Long.valueOf(solicitud.getIdUser()))
                    .flatMap(
                        usuario ->
                            solicitudRepository
                                .obtenerSolicitudesPorIdUser(solicitud.getIdUser())
                                .flatMap(
                                    solicitudes ->
                                        capacidadEndeudamientoGateway
                                            .enviarSolicitudCapacidadEndeudamiento(
                                                usuario,
                                                solicitud,
                                                solicitudes,
                                                solicitud.getIdSolicitud())
                                            .then(Mono.just(solicitud))))
                    .defaultIfEmpty(solicitud);
              } else {
                return Mono.just(solicitud);
              }
            })
        .defaultIfEmpty(solicitud);
  }
}
