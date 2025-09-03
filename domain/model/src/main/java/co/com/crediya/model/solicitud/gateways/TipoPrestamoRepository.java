package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(Long idTipoPrestamo);
    Mono<TipoPrestamo> findByNombre(String nombre);
    Mono<Boolean> existeByNombre(String nombre);
    Mono<Boolean> existeById(Long idTipoPrestamo);
}
