package co.com.crediya.api;

import co.com.crediya.api.security.AuthorizationService;
import co.com.crediya.api.docs.SolicitudControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Arrays;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class SolicitudRouterRest implements SolicitudControllerDocs {

    private final AuthorizationService authorizationService;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler) {
        return route(POST("/api/v1/solicitud"),
                request -> authorizationService.authorizeRoles(
                        request,
                        Arrays.asList("cliente"),
                        handler::escucharCrearSolicitud
                )
        ).andRoute(GET("/api/v1/solicitud"),
                request -> authorizationService.authorizeRoles(
                        request,
                        Arrays.asList("asesor"),
                        handler::escucharSolicitudesPaginadas
                )
        );
    }
}
