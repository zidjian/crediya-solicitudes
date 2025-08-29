package co.com.crediya.api.docs;

import co.com.crediya.api.SolicitudHandler;
import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.dto.RespuestaSolicitudDTO;
import co.com.crediya.shared.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Tag(name = "Solicitudes", description = "Operaciones relacionadas con la gestión de solicitudes de crédito")
public interface SolicitudControllerDocs {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "escucharCrearSolicitud",
                    operation = @Operation(
                            operationId = "createSolicitud",
                            summary = "Crear solicitud de crédito",
                            description = "Crea una nueva solicitud de crédito con validación de cliente y préstamo. Valida que el cliente exista y que el tipo de préstamo sea válido antes de crear la solicitud.",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud de crédito",
                                    content = @Content(schema = @Schema(implementation = CrearSolicitudDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = RespuestaSolicitudDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "422", description = "Tipo de préstamo inválido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler);
}
