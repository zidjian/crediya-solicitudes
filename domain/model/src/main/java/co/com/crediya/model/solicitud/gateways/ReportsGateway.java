package co.com.crediya.model.solicitud.gateways;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ReportsGateway {
    Mono<String> enviarReporteSolicitudesAprobadas(Long totalSolicitudesAprobadas, BigDecimal montoTotalAprobado);
}