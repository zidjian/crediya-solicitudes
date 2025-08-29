package co.com.crediya.r2dbcmysql.mapper;

import co.com.crediya.model.solicitud.TipoPrestamoConfig;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import co.com.crediya.r2dbcmysql.entities.TipoPrestamoEntity;
import co.com.crediya.r2dbcmysql.services.EstadoTipoPrestamoMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoPrestamoEntityMapper {

    private final EstadoTipoPrestamoMappingService mappingService;

    public TipoPrestamoConfig toDomain(TipoPrestamoEntity entity) {
        TipoPrestamo tipoPrestamo = mappingService.getTipoPrestamoById(entity.getIdTipoPrestamo());

        return new TipoPrestamoConfig(
                entity.getIdTipoPrestamo(),
                tipoPrestamo,
                entity.getNombre(),
                entity.getMontoMinimo(),
                entity.getMontoMaximo(),
                entity.getTasaInteres(),
                entity.getValidacionAutomatica()
        );
    }

    public TipoPrestamoEntity toEntity(TipoPrestamoConfig config) {
        return new TipoPrestamoEntity(
                config.getIdTipoPrestamo(),
                config.getNombre(),
                config.getMontoMinimo(),
                config.getMontoMaximo(),
                config.getTasaInteres(),
                config.isValidacionAutomatica()
        );
    }
}
