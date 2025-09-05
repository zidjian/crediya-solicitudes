package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.common.PageRequest;
import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.exceptions.AlreadyExistException;
import co.com.crediya.usecase.solicitud.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class SolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Mono<Solicitud> crearSolicitud(String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo, Long idTipoPrestamo) {
        return validarSolicitudNoExiste(documentoIdentidad)
                .then(validarIdTipoPrestamo(idTipoPrestamo))
                .flatMap(validatedIdTipoPrestamo -> validarTipoPrestamoExiste(validatedIdTipoPrestamo)
                        .then(validarMontoParaTipoPrestamo(monto, validatedIdTipoPrestamo))
                        .then(estadoRepository.obtenerIdEstadoPendienteRevision())
                        .map(idEstadoPendiente -> Solicitud.toSolicitud(documentoIdentidad, email, monto, plazo, validatedIdTipoPrestamo, idEstadoPendiente)))
                .flatMap(solicitudRepository::crear);
    }

    public Mono<PageResult<Solicitud>> obtenerSolicitudesPaginadas(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        return solicitudRepository.obtenerSolicitudes(pageRequest);
    }

    private Mono<Void> validarSolicitudNoExiste(String documentoIdentidad) {
        return solicitudRepository.existePorDocumentoIdentidad(documentoIdentidad.trim())
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new AlreadyExistException(
                                String.format("Ya existe una solicitud para el documento de identidad: %s", documentoIdentidad)));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Long> validarIdTipoPrestamo(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new ValidationException("El ID del tipo de préstamo es obligatorio"));
        }

        return Mono.just(idTipoPrestamo);
    }

    private Mono<Void> validarTipoPrestamoExiste(Long idTipoPrestamo) {
        return tipoPrestamoRepository.existeById(idTipoPrestamo)
                .flatMap(existe -> {
                    if (!existe) {
                        return Mono.error(new ValidationException(String.format("No existe un tipo de préstamo con ID: %s", idTipoPrestamo)));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validarMontoParaTipoPrestamo(BigDecimal monto, Long idTipoPrestamo) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new ValidationException("El monto debe ser mayor a cero"));
        }

        return tipoPrestamoRepository.findById(idTipoPrestamo)
                .flatMap(tipoPrestamo -> {
                    if (!tipoPrestamo.validarMonto(monto)) {
                        return Mono.error(new ValidationException(String.format("El monto %s no es válido para el tipo de préstamo. Debe estar entre %s y %s",
                                monto, tipoPrestamo.getMontoMinimo(), tipoPrestamo.getMontoMaximo())));
                    }
                    return Mono.empty();
                });
    }
}
