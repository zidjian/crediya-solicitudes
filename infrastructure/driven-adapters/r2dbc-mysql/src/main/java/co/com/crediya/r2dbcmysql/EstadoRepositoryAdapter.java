package co.com.crediya.r2dbcmysql;

import co.com.crediya.model.solicitud.Estado;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.r2dbcmysql.mapper.EstadoEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EstadoRepositoryAdapter implements EstadoRepository {

    private final EstadoReactiveRepository repository;
    private final EstadoEntityMapper mapper;

    @Override
    public Mono<Estado> findById(Long idEstado) {
        log.debug("[ESTADO_ADAPTER] Buscando estado por ID: {}", idEstado);
        return repository.findById(idEstado)
                .map(mapper::toDomain)
                .doOnSuccess(estado -> log.debug("[ESTADO_ADAPTER] Estado encontrado: {}", estado))
                .doOnError(error -> log.error("[ESTADO_ADAPTER] Error al buscar estado por ID {}: {}", idEstado, error.getMessage()));
    }

    @Override
    public Mono<Estado> findByNombre(String nombre) {
        log.debug("[ESTADO_ADAPTER] Buscando estado por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(mapper::toDomain)
                .doOnSuccess(estado -> log.debug("[ESTADO_ADAPTER] Estado encontrado por nombre: {}", estado))
                .doOnError(error -> log.error("[ESTADO_ADAPTER] Error al buscar estado por nombre {}: {}", nombre, error.getMessage()));
    }

    @Override
    public Mono<Boolean> existeByNombre(String nombre) {
        log.debug("[ESTADO_ADAPTER] Verificando existencia de estado por nombre: {}", nombre);
        return repository.existsByNombre(nombre)
                .doOnSuccess(existe -> log.debug("[ESTADO_ADAPTER] Estado {} existe: {}", nombre, existe))
                .doOnError(error -> log.error("[ESTADO_ADAPTER] Error al verificar existencia de estado {}: {}", nombre, error.getMessage()));
    }

    @Override
    public Mono<Long> obtenerIdEstadoPendienteRevision() {
        log.debug("[ESTADO_ADAPTER] Obteniendo ID del estado 'Pendiente de revision'");
        return repository.findByNombre("Pendiente de revision")
                .map(estadoEntity -> estadoEntity.getIdEstado())
                .switchIfEmpty(Mono.error(new IllegalStateException("No se encontró el estado 'Pendiente de revision' en la base de datos")))
                .doOnSuccess(idEstado -> log.debug("[ESTADO_ADAPTER] ID del estado 'Pendiente de revision': {}", idEstado))
                .doOnError(error -> log.error("[ESTADO_ADAPTER] Error al obtener ID del estado 'Pendiente de revision': {}", error.getMessage()));
    }
}
