package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.Estado;
import reactor.core.publisher.Mono;

public interface EstadoRepository {
  Mono<Estado> findById(Long idEstado);

  Mono<Estado> findByNombre(String nombre);

  Mono<Boolean> existeByNombre(String nombre);

  Mono<Long> obtenerIdEstadoPendienteRevision();
}
