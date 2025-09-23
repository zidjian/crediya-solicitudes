package co.com.crediya.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface NotificationGateway {
  Mono<String> enviarNotificacionEstadoSolicitud(String email, String estado, Long idSolicitud);
}
