package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Estado;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.usecase.solicitud.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EstadoUseCase {
  private final EstadoRepository estadoRepository;

  public Mono<Estado> findById(Long idEstado) {
    if (idEstado == null) {
      return Mono.error(new IllegalArgumentException("El id del estado no puede ser nulo"));
    }

    return estadoRepository
        .findById(idEstado)
        .switchIfEmpty(
            Mono.error(new IllegalStateException("No existe un estado con id: " + idEstado)))
        .onErrorMap(
            throwable -> {
              // Si ya es una BusinessException, la dejamos pasar
              if (throwable instanceof NotFoundException) {
                return throwable;
              }
              return new IllegalStateException("Error al obtener el estado con id: " + idEstado);
            });
  }
}
