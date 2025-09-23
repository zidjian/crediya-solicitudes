package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadEndeudamientoGateway {
  Mono<String> enviarSolicitudCapacidadEndeudamiento(
      Usuario usuario, Solicitud solicitud, List<Solicitud> solicitudes, Long idSolicitudActual);
}
