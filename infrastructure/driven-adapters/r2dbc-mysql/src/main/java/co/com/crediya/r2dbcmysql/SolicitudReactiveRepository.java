package co.com.crediya.r2dbcmysql;

import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {
    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);

    Flux<SolicitudEntity> findAllByOrderByIdSolicitudDesc(Pageable pageable);
}
