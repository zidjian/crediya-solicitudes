package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RespuestaSolicitudDTO;
import co.com.crediya.model.solicitud.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudDTOMapper {

    public RespuestaSolicitudDTO toResponse(Solicitud solicitud) {
        return new RespuestaSolicitudDTO(
                solicitud.getIdSolicitud(),
                solicitud.getDocumentoIdentidad(),
                solicitud.getEmail(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getTipoPrestamo().name(),
                solicitud.getEstado().getDescripcion(),
                solicitud.getFechaCreacion(),
                solicitud.getFechaActualizacion()
        );
    }
}
