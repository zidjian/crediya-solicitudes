package co.com.crediya.r2dbcmysql;

import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbcmysql.mapper.TipoPrestamoEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoReactiveRepository repository;
    private final TipoPrestamoEntityMapper mapper;

    @Override
    public Mono<TipoPrestamo> findById(Long idTipoPrestamo) {
        log.debug("[TIPO_PRESTAMO_ADAPTER] Buscando tipo de préstamo por ID: {}", idTipoPrestamo);
        return repository.findById(idTipoPrestamo)
                .map(mapper::toDomain)
                .doOnSuccess(tipoPrestamo -> log.debug("[TIPO_PRESTAMO_ADAPTER] Tipo de préstamo encontrado: {}", tipoPrestamo))
                .doOnError(error -> log.error("[TIPO_PRESTAMO_ADAPTER] Error al buscar tipo de préstamo por ID {}: {}", idTipoPrestamo, error.getMessage()));
    }

    @Override
    public Mono<TipoPrestamo> findByNombre(String nombre) {
        log.debug("[TIPO_PRESTAMO_ADAPTER] Buscando tipo de préstamo por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(mapper::toDomain)
                .doOnSuccess(tipoPrestamo -> log.debug("[TIPO_PRESTAMO_ADAPTER] Tipo de préstamo encontrado por nombre: {}", tipoPrestamo))
                .doOnError(error -> log.error("[TIPO_PRESTAMO_ADAPTER] Error al buscar tipo de préstamo por nombre {}: {}", nombre, error.getMessage()));
    }

    @Override
    public Mono<Boolean> existeByNombre(String nombre) {
        log.debug("[TIPO_PRESTAMO_ADAPTER] Verificando existencia de tipo de préstamo por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .hasElement()
                .doOnSuccess(existe -> log.debug("[TIPO_PRESTAMO_ADAPTER] Tipo de préstamo {} existe: {}", nombre, existe))
                .doOnError(error -> log.error("[TIPO_PRESTAMO_ADAPTER] Error al verificar existencia de tipo de préstamo {}: {}", nombre, error.getMessage()));
    }

    @Override
    public Mono<Boolean> existeById(Long idTipoPrestamo) {
        log.debug("[TIPO_PRESTAMO_ADAPTER] Verificando existencia de tipo de préstamo por ID: {}", idTipoPrestamo);
        return repository.existsById(idTipoPrestamo)
                .doOnSuccess(existe -> log.debug("[TIPO_PRESTAMO_ADAPTER] Tipo de préstamo con ID {} existe: {}", idTipoPrestamo, existe))
                .doOnError(error -> log.error("[TIPO_PRESTAMO_ADAPTER] Error al verificar existencia de tipo de préstamo con ID {}: {}", idTipoPrestamo, error.getMessage()));
    }
}
