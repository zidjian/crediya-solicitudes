package co.com.crediya.r2dbcmysql.mapper;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.enums.EstadoSolicitud;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import co.com.crediya.r2dbcmysql.services.EstadoTipoPrestamoMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitudEntityMapper {

    private final EstadoTipoPrestamoMappingService mappingService;

    public SolicitudEntity toEntity(Solicitud solicitud) {
        return new SolicitudEntity(
                solicitud.getIdSolicitud(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getEmail(),
                solicitud.getDocumentoIdentidad(),
                mappingService.getEstadoId(solicitud.getEstado()),
                mappingService.getTipoPrestamoId(solicitud.getTipoPrestamo()),
                solicitud.getFechaCreacion(),
                solicitud.getFechaActualizacion()
        );
    }

    public Solicitud toDomain(SolicitudEntity entity) {
        EstadoSolicitud estado = mappingService.getEstadoById(entity.getIdEstado());
        TipoPrestamo tipoPrestamo = mappingService.getTipoPrestamoById(entity.getIdTipoPrestamo());

        return Solicitud.crear(
                entity.getDocumentoIdentidad(),
                entity.getEmail(),
                entity.getMonto(),
                entity.getPlazo(),
                tipoPrestamo
        )
        .conId(entity.getIdSolicitud())
        .cambiarEstado(estado);
    }
}
