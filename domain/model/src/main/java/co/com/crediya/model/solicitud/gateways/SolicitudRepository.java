package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {
    Mono<Solicitud> crear(Solicitud solicitud);
    Mono<Boolean> existePorDocumentoIdentidad(String documentoIdentidad);
    Mono<PageResult<Solicitud>> obtenerSolicitudes(int page, int size);
    Mono<Solicitud> actualizar(Solicitud solicitud);
    Mono<Solicitud> findById(Long idSolicitud);
    Mono<Boolean> existeById(Long idSolicitud);
}
