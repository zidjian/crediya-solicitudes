package co.com.crediya.sqs.reportes;

import co.com.crediya.model.solicitud.gateways.ReportsGateway;
import co.com.crediya.sqs.sender.SQSSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
public class ReportsAdapter implements ReportsGateway {

    private final SQSSender sqsSender;

    @Override
    public Mono<String> enviarReporteSolicitudesAprobadas(Long totalSolicitudesAprobadas, BigDecimal montoTotalAprobado) {
        return crearMensajeReporte(totalSolicitudesAprobadas, montoTotalAprobado)
                .doOnNext(mensaje ->
                        log.info("[SQS_REPORTES] Enviando reporte de solicitudes aprobadas - Total: {}, Monto: {}",
                                totalSolicitudesAprobadas, montoTotalAprobado))
                .flatMap(mensaje ->
                        sqsSender.send(mensaje, "https://sqs.us-east-1.amazonaws.com/920004595108/cola-reportes"))
                .doOnSuccess(messageId ->
                        log.info("[SQS_REPORTES] Reporte enviado exitosamente. MessageId: {}", messageId))
                .doOnError(ex ->
                        log.error("[SQS_REPORTES] Error enviando reporte: {}", ex.getMessage()));
    }

    private Mono<String> crearMensajeReporte(Long totalSolicitudesAprobadas, BigDecimal montoTotalAprobado) {
        return Mono.fromCallable(() -> {
            LocalDateTime fechaHora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            return String.format("""
                    {
                        "accion": "actualizarSolicitud",
                        "fechaHora": "%s",
                        "totalSolicitudesAprobadas": %d,
                        "montoTotalAprobado": %.2f
                    }""",
                    fechaHora.format(formatter),
                    totalSolicitudesAprobadas,
                    montoTotalAprobado.doubleValue());
        });
    }
}