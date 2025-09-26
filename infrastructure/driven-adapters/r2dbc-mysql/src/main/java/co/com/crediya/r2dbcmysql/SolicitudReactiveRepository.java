package co.com.crediya.r2dbcmysql;

import co.com.crediya.r2dbcmysql.entities.SolicitudEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface SolicitudReactiveRepository
    extends ReactiveCrudRepository<SolicitudEntity, Long>,
        ReactiveQueryByExampleExecutor<SolicitudEntity> {
  Mono<Boolean> existsByIdUser(String idUser);

  Flux<SolicitudEntity> findAllByOrderByIdSolicitudDesc(Pageable pageable);

  Flux<SolicitudEntity> findAllByIdUser(String idUser);
  
  @Query("SELECT COUNT(*) FROM solicitud s JOIN estado e ON s.id_estado = e.id_estado WHERE e.nombre = 'Aprobada'")
  Mono<Long> countByEstadoAprobado();
  
  @Query("SELECT COALESCE(SUM(s.monto), 0) FROM solicitud s JOIN estado e ON s.id_estado = e.id_estado WHERE e.nombre = 'Aprobada'")
  Mono<BigDecimal> sumMontoByEstadoAprobado();
}
