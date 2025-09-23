package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RespuestaSolicitudDTO;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SolicitudDTOMapper {

  private final TipoPrestamoRepository tipoPrestamoRepository;
  private final EstadoRepository estadoRepository;

  public Mono<RespuestaSolicitudDTO> toResponse(Solicitud solicitud) {
    return Mono.zip(
            tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo()),
            estadoRepository.findById(solicitud.getIdEstado()))
        .map(
            tuple -> {
              var tipoPrestamo = tuple.getT1();
              var estado = tuple.getT2();

              // Calcular deuda total mensual
              BigDecimal deudaTotalMensual =
                  solicitud.calcularDeudaTotalMensual(tipoPrestamo.getTasaInteres());

              return new RespuestaSolicitudDTO(
                  solicitud.getIdSolicitud(),
                  solicitud.getIdUser(),
                  solicitud.getEmail(),
                  solicitud.getMonto(),
                  solicitud.getPlazo(),
                  tipoPrestamo.getNombre(),
                  estado.getNombre(),
                  tipoPrestamo.getTasaInteres(),
                  deudaTotalMensual);
            });
  }
}
