package co.com.crediya.r2dbcmysql;

import co.com.crediya.model.solicitud.TipoPrestamoConfig;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbcmysql.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.r2dbcmysql.services.EstadoTipoPrestamoMappingService;
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
    private final EstadoTipoPrestamoMappingService mappingService;

    @Override
    public Mono<TipoPrestamoConfig> obtenerConfiguracionPorTipo(TipoPrestamo tipo) {
        log.debug("[TIPO_PRESTAMO_ADAPTER] Obteniendo configuración desde BD para tipo: {}", tipo);

        // Obtener el ID del tipo de préstamo usando el servicio de mapeo
        Long tipoId = mappingService.getTipoPrestamoId(tipo);

        if (tipoId == null) {
            log.warn("[TIPO_PRESTAMO_ADAPTER] No se encontró ID para el tipo: {}", tipo);
            return Mono.empty();
        }

        return repository.findById(tipoId)
                .map(mapper::toDomain)
                .doOnSuccess(config -> log.debug("[TIPO_PRESTAMO_ADAPTER] Configuración obtenida desde BD para tipo: {} con montos: {}-{}",
                        tipo, config.getMontoMinimo(), config.getMontoMaximo()))
                .doOnError(error -> log.error("[TIPO_PRESTAMO_ADAPTER] Error al obtener configuración para tipo {}: {}",
                        tipo, error.getMessage()));
    }
}
