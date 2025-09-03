package co.com.crediya.r2dbcmysql.mapper;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitudEntityMapper {

    public SolicitudEntity toEntity(Solicitud solicitud) {
        return new SolicitudEntity(
                solicitud.getIdSolicitud(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getEmail(),
                solicitud.getDocumentoIdentidad(),
                solicitud.getIdEstado(),
                solicitud.getIdTipoPrestamo()
        );
    }

    public Solicitud toDomain(SolicitudEntity entity) {
        return Solicitud.toSolicitud(
                entity.getDocumentoIdentidad(),
                entity.getEmail(),
                entity.getMonto(),
                entity.getPlazo(),
                entity.getIdTipoPrestamo(),
                entity.getIdEstado()
        );
    }
}
