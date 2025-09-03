package co.com.crediya.r2dbcmysql.mapper;

import co.com.crediya.model.solicitud.Estado;
import co.com.crediya.r2dbcmysql.entities.EstadoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoEntityMapper {

    Estado toDomain(EstadoEntity entity);

    EstadoEntity toEntity(Estado domain);

    default Estado toDomain(EstadoEntity entity, Estado domain) {
        if (entity == null) {
            return null;
        }
        return Estado.toEstado(
                entity.getIdEstado(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }
}
