package co.com.crediya.r2dbcmysql;

import co.com.crediya.r2dbcmysql.entities.EstadoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface EstadoReactiveRepository
    extends ReactiveCrudRepository<EstadoEntity, Long>,
        ReactiveQueryByExampleExecutor<EstadoEntity> {
  Mono<EstadoEntity> findByNombre(String nombre);

  Mono<Boolean> existsByNombre(String nombre);
}
