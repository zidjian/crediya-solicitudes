package co.com.crediya.r2dbcmysql.services;

import co.com.crediya.model.solicitud.enums.EstadoSolicitud;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EstadoTipoPrestamoMappingService {

    // Mapeos basados en los datos reales del changelog de Liquibase
    // Estados: id_estado = 1,2,3,4 para los registros insertados en orden
    private static final Map<EstadoSolicitud, Long> ESTADO_TO_ID = Map.of(
        EstadoSolicitud.PENDIENTE_REVISION, 1L,  // "Pendiente de revision"
        EstadoSolicitud.APROBADA, 2L,            // "Aprobada"
        EstadoSolicitud.RECHAZADA, 3L,           // "Rechazada"
        EstadoSolicitud.CANCELADA, 4L            // "Cancelada"
    );

    private static final Map<Long, EstadoSolicitud> ID_TO_ESTADO = Map.of(
        1L, EstadoSolicitud.PENDIENTE_REVISION,
        2L, EstadoSolicitud.APROBADA,
        3L, EstadoSolicitud.RECHAZADA,
        4L, EstadoSolicitud.CANCELADA
    );

    // Tipos de préstamo: id_tipo_prestamo = 1,2,3,4,5 para los registros insertados en orden
    private static final Map<TipoPrestamo, Long> TIPO_TO_ID = Map.of(
        TipoPrestamo.PERSONAL, 1L,     // "Personal"
        TipoPrestamo.VEHICULAR, 2L,    // "Vehicular"
        TipoPrestamo.HIPOTECARIO, 3L,  // "Hipotecario"
        TipoPrestamo.COMERCIAL, 4L,    // "Comercial"
        TipoPrestamo.EDUCATIVO, 5L     // "Educativo"
    );

    private static final Map<Long, TipoPrestamo> ID_TO_TIPO = Map.of(
        1L, TipoPrestamo.PERSONAL,
        2L, TipoPrestamo.VEHICULAR,
        3L, TipoPrestamo.HIPOTECARIO,
        4L, TipoPrestamo.COMERCIAL,
        5L, TipoPrestamo.EDUCATIVO
    );

    public Long getEstadoId(EstadoSolicitud estado) {
        return ESTADO_TO_ID.get(estado);
    }

    public EstadoSolicitud getEstadoById(Long id) {
        return ID_TO_ESTADO.get(id);
    }

    public Long getTipoPrestamoId(TipoPrestamo tipo) {
        return TIPO_TO_ID.get(tipo);
    }

    public TipoPrestamo getTipoPrestamoById(Long id) {
        return ID_TO_TIPO.get(id);
    }
}
