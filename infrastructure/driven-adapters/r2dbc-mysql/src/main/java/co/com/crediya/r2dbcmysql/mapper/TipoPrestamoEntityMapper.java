package co.com.crediya.r2dbcmysql.mapper;

import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.r2dbcmysql.entities.TipoPrestamoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class TipoPrestamoEntityMapper {
  public TipoPrestamo toEntity(TipoPrestamo domain) {
    if (domain == null) {
      return null;
    }
    return TipoPrestamo.toTipoPrestamo(
        domain.getIdTipoPrestamo(),
        domain.getNombre(),
        domain.getMontoMinimo(),
        domain.getMontoMaximo(),
        domain.getTasaInteres(),
        domain.isValidacionAutomatica());
  }

  public TipoPrestamo toDomain(TipoPrestamoEntity entity) {
    if (entity == null) {
      return null;
    }
    return TipoPrestamo.toTipoPrestamo(
        entity.getIdTipoPrestamo(),
        entity.getNombre(),
        entity.getMontoMinimo(),
        entity.getMontoMaximo(),
        entity.getTasaInteres(),
        entity.getValidacionAutomatica());
  }
}
