package co.com.crediya.r2dbcmysql;

import co.com.crediya.r2dbcmysql.entities.TipoPrestamoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface TipoPrestamoReactiveRepository
    extends ReactiveCrudRepository<TipoPrestamoEntity, Long>,
        ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {
  Mono<TipoPrestamoEntity> findByNombre(String nombre);
}
