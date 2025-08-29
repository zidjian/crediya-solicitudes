package co.com.crediya.r2dbcmysql;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import co.com.crediya.r2dbcmysql.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbcmysql.mapper.SolicitudEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        SolicitudReactiveRepository
> implements SolicitudRepository {

    private final SolicitudEntityMapper solicitudEntityMapper;

    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository,
                                            ObjectMapper mapper,
                                            SolicitudEntityMapper solicitudEntityMapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
        this.solicitudEntityMapper = solicitudEntityMapper;
    }

    @Override
    public Mono<Solicitud> crear(Solicitud solicitud) {
        log.debug("[SOLICITUD_ADAPTER] Creando solicitud para documento: {}", solicitud.getDocumentoIdentidad());
        SolicitudEntity entity = solicitudEntityMapper.toEntity(solicitud);
        return super.repository.save(entity)
                .map(solicitudEntityMapper::toDomain)
                .doOnSuccess(s -> log.debug("[SOLICITUD_ADAPTER] Solicitud creada con ID: {}", s.getIdSolicitud()))
                .doOnError(error -> log.error("[SOLICITUD_ADAPTER] Error guardando solicitud: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> existePorDocumentoIdentidad(String documentoIdentidad) {
        log.debug("[SOLICITUD_ADAPTER] Validando existencia de solicitud para documento: {}", documentoIdentidad);
        return super.repository.existsByDocumentoIdentidad(documentoIdentidad)
                .doOnNext(existe -> log.debug("[SOLICITUD_ADAPTER] Documento {} existe: {}", documentoIdentidad, existe));
    }
}
