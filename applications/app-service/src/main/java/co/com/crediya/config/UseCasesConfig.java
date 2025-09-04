package co.com.crediya.config;

import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbcmysql.TipoPrestamoRepositoryAdapter;
import co.com.crediya.r2dbcmysql.TipoPrestamoReactiveRepository;
import co.com.crediya.r2dbcmysql.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public TipoPrestamoRepository tipoPrestamoRepository(
            TipoPrestamoReactiveRepository repository,
            TipoPrestamoEntityMapper mapper) {
        return new TipoPrestamoRepositoryAdapter(repository, mapper);
    }

    @Bean
    public SolicitudUseCase solicitudUseCase(SolicitudRepository solicitudRepository,
                                           TipoPrestamoRepository tipoPrestamoRepository,
                                           EstadoRepository estadoRepository) {
        return new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository);
    }
}
