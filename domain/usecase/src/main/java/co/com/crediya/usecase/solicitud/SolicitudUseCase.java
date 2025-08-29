package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.TipoPrestamoConfig;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.model.usuario.gateways.UsuarioValidacionRepository;
import co.com.crediya.usecase.solicitud.exceptions.MontoInvalidoException;
import co.com.crediya.usecase.solicitud.exceptions.TipoPrestamoInvalidoException;
import co.com.crediya.usecase.solicitud.exceptions.UsuarioNoExisteException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class SolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioValidacionRepository usuarioValidacionRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<Solicitud> crearSolicitud(String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo, String tipoPrestamoStr) {
        return validarUsuarioExiste(documentoIdentidad)
                .then(validarTipoPrestamo(tipoPrestamoStr))
                .flatMap(tipoPrestamo -> validarMontoParaTipoPrestamo(monto, tipoPrestamo)
                        .map(config -> tipoPrestamo))
                .map(tipoPrestamo -> Solicitud.crear(documentoIdentidad, email, monto, plazo, tipoPrestamo))
                .flatMap(solicitudRepository::crear);
    }

    private Mono<Void> validarUsuarioExiste(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            return Mono.error(new IllegalArgumentException("El documento de identidad es obligatorio"));
        }

        return usuarioValidacionRepository.existeUsuarioPorDocumento(documentoIdentidad.trim())
                .flatMap(existe -> {
                    if (!existe) {
                        return Mono.error(new UsuarioNoExisteException(documentoIdentidad));
                    }
                    return Mono.empty();
                });
    }

    private Mono<TipoPrestamo> validarTipoPrestamo(String tipoPrestamoStr) {
        if (tipoPrestamoStr == null || tipoPrestamoStr.isBlank()) {
            return Mono.error(new IllegalArgumentException("El tipo de préstamo es obligatorio"));
        }

        try {
            TipoPrestamo tipoPrestamo = TipoPrestamo.valueOf(tipoPrestamoStr.toUpperCase().trim());
            return Mono.just(tipoPrestamo);
        } catch (IllegalArgumentException e) {
            return Mono.error(new TipoPrestamoInvalidoException(tipoPrestamoStr));
        }
    }

    private Mono<TipoPrestamoConfig> validarMontoParaTipoPrestamo(BigDecimal monto, TipoPrestamo tipoPrestamo) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El monto debe ser mayor a cero"));
        }

        return tipoPrestamoRepository.obtenerConfiguracionPorTipo(tipoPrestamo)
                .flatMap(config -> {
                    if (!config.validarMonto(monto)) {
                        return Mono.error(new MontoInvalidoException(monto, tipoPrestamo,
                                config.getMontoMinimo(), config.getMontoMaximo()));
                    }
                    return Mono.just(config);
                });
    }
}
