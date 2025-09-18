package co.com.crediya.api.docs;

import co.com.crediya.api.SolicitudHandler;
import co.com.crediya.api.dto.ActualizarSolicitudDTO;
import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.dto.PaginatedResponseDTO;
import co.com.crediya.api.dto.RespuestaSolicitudDTO;
import co.com.crediya.shared.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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
                            security = @SecurityRequirement(name = "bearerAuth"),
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
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/user/{idUser}",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "escucharSolicitudesPorUsuario",
                    operation = @Operation(
                            operationId = "getSolicitudesByUser",
                            summary = "Obtener todas las solicitudes de un usuario",
                            description = "Obtiene una lista completa de todas las solicitudes de crédito asociadas a un usuario específico identificado por su ID. No aplica paginación.",
                            tags = {"Solicitudes"},
                            security = @SecurityRequirement(name = "bearerAuth"),
                            parameters = {
                                    @Parameter(
                                            name = "idUser",
                                            in = ParameterIn.PATH,
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas exitosamente",
                                            content = @Content(schema = @Schema(type = "array", implementation = RespuestaSolicitudDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "ID de usuario inválido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "escucharSolicitudesPaginadas",
                    operation = @Operation(
                            operationId = "getSolicitudes",
                            summary = "Obtener solicitudes paginadas",
                            description = "Obtiene una lista paginada de solicitudes de crédito del usuario autenticado. Permite navegar a través de múltiples páginas de resultados.",
                            tags = {"Solicitudes"},
                            security = @SecurityRequirement(name = "bearerAuth"),
                            parameters = {
                                    @Parameter(
                                            name = "pagina",
                                            description = "Número de página (inicia en 0)",
                                            example = "0",
                                            schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "tamanio",
                                            description = "Cantidad de elementos por página",
                                            example = "10",
                                            schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "10")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas exitosamente",
                                            content = @Content(schema = @Schema(implementation = PaginatedResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.PUT,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "escucharActualizarSolicitud",
                    operation = @Operation(
                            operationId = "updateSolicitud",
                            summary = "Actualizar estado de solicitud",
                            description = "Actualiza el estado de una solicitud de crédito existente. Envía una notificación SQS cuando se actualiza el estado.",
                            tags = {"Solicitudes"},
                            security = @SecurityRequirement(name = "bearerAuth"),
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de actualización de la solicitud",
                                    content = @Content(schema = @Schema(implementation = ActualizarSolicitudDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitud actualizada exitosamente",
                                            content = @Content(schema = @Schema(implementation = RespuestaSolicitudDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler);
}
