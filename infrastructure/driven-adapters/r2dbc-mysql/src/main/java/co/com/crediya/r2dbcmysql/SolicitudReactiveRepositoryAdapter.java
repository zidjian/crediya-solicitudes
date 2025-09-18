package co.com.crediya.r2dbcmysql;

import co.com.crediya.model.common.PageResult;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import co.com.crediya.r2dbcmysql.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbcmysql.mapper.SolicitudEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

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
        log.debug("[SOLICITUD_ADAPTER] Creando solicitud para idUser: {}", solicitud.getIdUser());
        SolicitudEntity entity = solicitudEntityMapper.toEntity(solicitud);
        return super.repository.save(entity)
                .map(solicitudEntityMapper::toDomain)
                .doOnSuccess(s -> log.debug("[SOLICITUD_ADAPTER] Solicitud creada con ID: {}", s.getIdSolicitud()))
                .doOnError(error -> log.error("[SOLICITUD_ADAPTER] Error guardando solicitud: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> existePorIdUser(String idUser) {
        log.debug("[SOLICITUD_ADAPTER] Validando existencia de solicitud para idUser: {}", idUser);
        return super.repository.existsByIdUser(idUser);
    }

    @Override
    public Mono<List<Solicitud>> obtenerSolicitudesPorIdUser(String idUser) {
        log.debug("[SOLICITUD_ADAPTER] Obteniendo solicitudes por idUser: {}", idUser);
        return super.repository.findAllByIdUser(idUser)
                .map(solicitudEntityMapper::toDomain)
                .collectList()
                .doOnSuccess(solicitudes -> log.debug("[SOLICITUD_ADAPTER] Se encontraron {} solicitudes para idUser: {}",
                        solicitudes.size(), idUser));
    }

    @Override
    public Mono<PageResult<Solicitud>> obtenerSolicitudes(int page, int size) {
        log.debug("[SOLICITUD_ADAPTER] Obteniendo solicitudes paginadas - página: {}, tamañotamaño: {}",
                page, size);

        PageRequest springPageRequest =
                PageRequest.of(page, size);

        return super.repository.count()
                .flatMap(total -> {
                    if (total == 0) {
                        return Mono.just(new PageResult<Solicitud>(
                                List.of(),
                                page,
                                size,
                                total
                        ));
                    }

                    return super.repository.findAllByOrderByIdSolicitudDesc(springPageRequest)
                            .map(solicitudEntityMapper::toDomain)
                            .collectList()
                            .map(solicitudes -> new PageResult<>(
                                    solicitudes,
                                    page,
                                    size,
                                    total
                            ));
                })
                .doOnSuccess(result -> log.debug("[SOLICITUD_ADAPTER] Se obtuvieron {} solicitudes de {} totales",
                        result.content().size(), result.totalElements()));
    }

    @Override
    public Mono<Solicitud> actualizar(Solicitud solicitud) {
        log.debug("[SOLICITUD_ADAPTER] Actualizando solicitud ID: {}", solicitud.getIdSolicitud());
        SolicitudEntity entity = solicitudEntityMapper.toEntity(solicitud);
        return super.repository.save(entity)
                .map(solicitudEntityMapper::toDomain)
                .doOnSuccess(s -> log.debug("[SOLICITUD_ADAPTER] Solicitud actualizada con ID: {}", s.getIdSolicitud()))
                .doOnError(error -> log.error("[SOLICITUD_ADAPTER] Error actualizando solicitud: {}", error.getMessage()));
    }

    @Override
    public Mono<Solicitud> findById(Long idSolicitud) {
        log.debug("[SOLICITUD_ADAPTER] Buscando solicitud por ID: {}", idSolicitud);
        return super.repository.findById(idSolicitud)
                .map(solicitudEntityMapper::toDomain)
                .doOnSuccess(s -> log.debug("[SOLICITUD_ADAPTER] Solicitud encontrada: {}", s.getIdSolicitud()))
                .doOnError(error -> log.error("[SOLICITUD_ADAPTER] Error buscando solicitud por ID: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> existeById(Long idSolicitud) {
        log.debug("[SOLICITUD_ADAPTER] Validando existencia de solicitud por ID: {}", idSolicitud);
        return super.repository.existsById(idSolicitud)
                .doOnSuccess(existe -> log.debug("[SOLICITUD_ADAPTER] Solicitud existe: {}", existe));
    }
}
