package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudRepository {
  Mono<Solicitud> crear(Solicitud solicitud);

  Mono<Boolean> existePorIdUser(String idUser);

  Mono<PageResult<Solicitud>> obtenerSolicitudes(int page, int size);

  Mono<List<Solicitud>> obtenerSolicitudesPorIdUser(String idUser);

  Mono<Solicitud> actualizar(Solicitud solicitud);

  Mono<Solicitud> findById(Long idSolicitud);

  Mono<Boolean> existeById(Long idSolicitud);
}
