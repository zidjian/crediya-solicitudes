package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TipoPrestamoUseCase {
    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<TipoPrestamo> findById(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("El id del tipo de préstamo no puede ser nulo"));
        }

        return tipoPrestamoRepository.findById(idTipoPrestamo)
                .switchIfEmpty(Mono.error(new NotFoundException("No existe un tipo de préstamo con id: " + idTipoPrestamo)))
                .onErrorMap(throwable -> {
                    // Si ya es una BusinessException, la dejamos pasar
                    if (throwable instanceof NotFoundException) {
                        return throwable;
                    }
                    // Para cualquier otro error, lo envolvemos en una RuntimeException
                    return new RuntimeException("Error al buscar el tipo de préstamo con id: " + idTipoPrestamo, throwable);
                });
    }
}
