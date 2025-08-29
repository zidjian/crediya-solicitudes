package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.TipoPrestamoConfig;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamoConfig> obtenerConfiguracionPorTipo(TipoPrestamo tipo);
}
