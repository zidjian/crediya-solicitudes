package co.com.crediya.sqs.notification;

import co.com.crediya.model.solicitud.gateways.NotificationGateway;
import co.com.crediya.sqs.sender.SQSSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class SQSNotificationAdapter implements NotificationGateway {

    private final SQSSender sqsSender;

    @Override
    public Mono<String> enviarNotificacionEstadoSolicitud(String email, String estado, Long idSolicitud) {
        return Mono.fromCallable(() -> crearMensajeNotificacion(email, estado, idSolicitud))
                .doOnNext(mensaje -> log.info("[SQS_NOTIFICATION] Enviando notificación para solicitud ID: {} a email: {} con estado: {}",
                    idSolicitud, email, estado))
                .flatMap(sqsSender::send)
                .doOnSuccess(messageId -> log.info("[SQS_NOTIFICATION] Notificación enviada exitosamente. MessageId: {} para solicitud ID: {}",
                    messageId, idSolicitud))
                .doOnError(ex -> log.error("[SQS_NOTIFICATION] Error enviando notificación para solicitud ID: {} - {}",
                    idSolicitud, ex.getMessage()));
    }

    private String crearMensajeNotificacion(String email, String estado, Long idSolicitud) {
        String subject = "Actualización de su Solicitud de Préstamo #" + idSolicitud;
        String body = construirCuerpoMensaje(estado, idSolicitud);

        // Crear JSON manualmente para evitar dependencias de Jackson
        return String.format("""
            {
                "to": "%s",
                "subject": "%s",
                "body": "%s"
            }
            """,
            escapeJson(email),
            escapeJson(subject),
            escapeJson(body)
        );
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private String construirCuerpoMensaje(String estado, Long idSolicitud) {
        return String.format("""
            <html>
            <body>
                <h2>Estimado solicitante,</h2>
                <p>Le informamos que su solicitud de préstamo <strong>#%d</strong> ha sido actualizada.</p>
                <p><strong>Estado actual:</strong> %s</p>
                <br>
                <p>Para más información, puede contactar con nuestro equipo de atención al cliente.</p>
                <br>
                <p>Saludos cordiales,<br>
                Equipo Crediya</p>
            </body>
            </html>
            """, idSolicitud, estado);
    }
}
