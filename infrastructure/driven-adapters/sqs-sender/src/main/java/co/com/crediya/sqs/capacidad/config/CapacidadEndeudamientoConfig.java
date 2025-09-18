package co.com.crediya.sqs.capacidad.config;

import co.com.crediya.model.solicitud.gateways.CapacidadEndeudamientoGateway;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.sqs.capacidad.CapacidadEndeudamientoAdapter;
import co.com.crediya.sqs.sender.SQSSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CapacidadEndeudamientoConfig {

    @Bean
    public CapacidadEndeudamientoGateway capacidadEndeudamientoGateway(SQSSender sqsSender, SolicitudRepository solicitudRepository,
                                                                       TipoPrestamoRepository tipoPrestamoRepository, EstadoRepository estadoRepository) {
        return new CapacidadEndeudamientoAdapter(sqsSender, solicitudRepository, tipoPrestamoRepository, estadoRepository);
    }
}