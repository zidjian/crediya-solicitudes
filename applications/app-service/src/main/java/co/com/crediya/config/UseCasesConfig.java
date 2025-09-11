package co.com.crediya.config;

import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.NotificationGateway;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public SolicitudUseCase solicitudUseCase(SolicitudRepository solicitudRepository,
                                           TipoPrestamoRepository tipoPrestamoRepository,
                                           EstadoRepository estadoRepository,
                                           NotificationGateway notificationGateway) {
        return new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository, notificationGateway);
    }
}
