package co.com.crediya.api;

import co.com.crediya.api.docs.SolicitudControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class SolicitudRouterRest implements SolicitudControllerDocs {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler) {
        return route(POST("/api/v1/solicitud"), handler::escucharCrearSolicitud)
                .andRoute(GET("/api/v1/solicitud"), handler::escucharSolicitudesPaginadas)
                .andRoute(GET("/api/v1/solicitud/user/{idUser}"), handler::escucharSolicitudesPorUsuario)
                .andRoute(PUT("/api/v1/solicitud"), handler::escucharActualizarSolicitud);
    }
}
