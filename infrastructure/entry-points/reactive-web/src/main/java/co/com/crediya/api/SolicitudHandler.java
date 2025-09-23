package co.com.crediya.api;

import co.com.crediya.api.dto.ActualizarSolicitudDTO;
import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.dto.PaginatedResponseDTO;
import co.com.crediya.api.dto.RespuestaSolicitudDTO;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.api.mapper.SolicitudDTOMapper;
import co.com.crediya.api.security.AuthorizationService;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import co.com.crediya.usecase.solicitud.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolicitudHandler {
  private final SolicitudUseCase solicitudUseCase;
  private final SolicitudDTOMapper mapper;
  private final Validator validator;
  private final AuthorizationService authorizationService;

  public Mono<ServerResponse> escucharCrearSolicitud(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(CrearSolicitudDTO.class)
        .doOnSubscribe(sub -> log.info("[CREAR_SOLICITUD] Petición recibida"))
        .flatMap(
            dto -> {
              Set<ConstraintViolation<CrearSolicitudDTO>> violaciones = validator.validate(dto);
              if (!violaciones.isEmpty()) {
                log.warn(
                    "[CREAR_SOLICITUD] Validación fallida: {} violación(es)", violaciones.size());
                return Mono.error(new ConstraintViolationException(violaciones));
              }

              // Validar que el idUser del token coincida con el de la petición
              return validateDocumentoOwnership(serverRequest, dto.idUser()).then(Mono.just(dto));
            })
        .flatMap(
            dto ->
                solicitudUseCase.crearSolicitud(
                    dto.idUser(),
                    dto.email(),
                    dto.monto(),
                    LocalDate.parse(dto.plazo()),
                    dto.idTipoPrestamo()))
        .doOnSuccess(
            solicitud ->
                log.info(
                    "[CREAR_SOLICITUD] Solicitud creada exitosamente con ID: {} para idUser: {}",
                    solicitud.getIdSolicitud(),
                    solicitud.getIdUser()))
        .flatMap(
            solicitudCreada ->
                ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.toResponse(solicitudCreada), RespuestaSolicitudDTO.class))
        .doOnError(
            ex -> log.error("[CREAR_SOLICITUD] Error procesando solicitud: {}", ex.getMessage()));
  }

  private Mono<Void> validateDocumentoOwnership(ServerRequest request, String idUserPeticion) {
    return authorizationService
        .extractIdUserFromToken(request)
        .flatMap(
            idUserToken -> {
              if (!idUserToken.equals(idUserPeticion)) {
                log.warn(
                    "[CREAR_SOLICITUD] idUser en token {} no coincide con idUser en petición {}",
                    idUserToken,
                    idUserPeticion);
                return Mono.error(
                    new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No puede crear solicitudes para otros usuarios"));
              }
              log.info(
                  "[CREAR_SOLICITUD] Validación de idUser exitosa: token={}, petición={}",
                  idUserToken,
                  idUserPeticion);
              return Mono.empty();
            });
  }

  public Mono<ServerResponse> escucharSolicitudesPaginadas(ServerRequest serverRequest) {
    return Mono.fromCallable(
            () -> {
              // Obtener parámetros de paginación de los query parameters
              int pagina = serverRequest.queryParam("pagina").map(Integer::parseInt).orElse(0);
              int tamanio = serverRequest.queryParam("tamanio").map(Integer::parseInt).orElse(10);

              // Validar parámetros
              if (pagina < 0) {
                log.warn("[GET_SOLICITUDES_PAGINADAS] Número de página inválido: {}", pagina);
                throw new ValidationException("El número de página debe ser mayor o igual a 0");
              }
              if (tamanio <= 0 || tamanio > 100) {
                log.warn("[GET_SOLICITUDES_PAGINADAS] Tamaño de página inválido: {}", tamanio);
                throw new ValidationException("El tamaño de página debe estar entre 1 y 100");
              }

              log.info(
                  "[GET_SOLICITUDES_PAGINADAS] Consultando todas las solicitudes, página: {}, tamaño: {}",
                  pagina,
                  tamanio);

              return new int[] {pagina, tamanio};
            })
        .doOnSubscribe(sub -> log.info("[GET_SOLICITUDES_PAGINADAS] Petición recibida"))
        .flatMap(params -> solicitudUseCase.obtenerSolicitudesPaginadas(params[0], params[1]))
        .doOnSuccess(
            pageResult ->
                log.info(
                    "[GET_SOLICITUDES_PAGINADAS] Se encontraron {} solicitudes de un total de {}",
                    pageResult.content().size(),
                    pageResult.totalElements()))
        .flatMap(
            pageResult ->
                // Convertir cada Solicitud a RespuestaSolicitudDTO resolviendo los Mono devueltos
                // por el mapper
                Flux.fromIterable(pageResult.content())
                    .flatMap(
                        mapper
                            ::toResponse) // mapper.toResponse devuelve Mono<RespuestaSolicitudDTO>
                    .collectList()
                    .flatMap(
                        solicitudesDTO -> {
                          // Crear la respuesta paginada con los DTOs ya resueltos
                          var paginatedResponse =
                              PaginatedResponseDTO.of(
                                  solicitudesDTO,
                                  pageResult.page(),
                                  pageResult.size(),
                                  pageResult.totalElements());

                          return ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .bodyValue(paginatedResponse);
                        }))
        .doOnError(
            ex ->
                log.error(
                    "[GET_SOLICITUDES_PAGINADAS] Error consultando solicitudes: {}",
                    ex.getMessage()));
  }

  public Mono<ServerResponse> escucharSolicitudesPorUsuario(ServerRequest serverRequest) {
    return Mono.fromCallable(
            () -> {
              String idUsuario = serverRequest.pathVariable("idUser");
              if (idUsuario == null || idUsuario.trim().isEmpty()) {
                log.warn("[GET_SOLICITUDES_POR_USUARIO] ID de usuario vacío o nulo");
                throw new ValidationException("El ID del usuario es obligatorio");
              }
              log.info(
                  "[GET_SOLICITUDES_POR_USUARIO] Consultando solicitudes para usuario ID: {}",
                  idUsuario);
              return idUsuario;
            })
        .doOnSubscribe(sub -> log.info("[GET_SOLICITUDES_POR_USUARIO] Petición recibida"))
        .flatMap(idUsuario -> solicitudUseCase.obtenerSolicitudesPorIdUsuario(idUsuario))
        .doOnSuccess(
            solicitudes ->
                log.info(
                    "[GET_SOLICITUDES_POR_USUARIO] Se encontraron {} solicitudes",
                    solicitudes.size()))
        .flatMap(
            solicitudes ->
                // Convertir cada Solicitud a RespuestaSolicitudDTO resolviendo los Mono devueltos
                // por el mapper
                Flux.fromIterable(solicitudes)
                    .flatMap(
                        mapper
                            ::toResponse) // mapper.toResponse devuelve Mono<RespuestaSolicitudDTO>
                    .collectList()
                    .flatMap(
                        solicitudesDTO ->
                            ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(solicitudesDTO)))
        .doOnError(
            ex ->
                log.error(
                    "[GET_SOLICITUDES_POR_USUARIO] Error consultando solicitudes por usuario: {}",
                    ex.getMessage()));
  }

  public Mono<ServerResponse> escucharActualizarSolicitud(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(ActualizarSolicitudDTO.class)
        .doOnSubscribe(sub -> log.info("[ACTUALIZAR_SOLICITUD] Petición recibida"))
        .flatMap(
            dto -> {
              Set<ConstraintViolation<ActualizarSolicitudDTO>> violaciones =
                  validator.validate(dto);
              if (!violaciones.isEmpty()) {
                log.warn(
                    "[ACTUALIZAR_SOLICITUD] Validación fallida: {} violación(es)",
                    violaciones.size());
                return Mono.error(new ConstraintViolationException(violaciones));
              }
              return Mono.just(dto);
            })
        .flatMap(
            dto -> {
              try {
                Long idSolicitud = dto.idSolicitud();
                Long idEstado = dto.idEstado();

                log.info(
                    "[ACTUALIZAR_SOLICITUD] Actualizando solicitud ID: {} con nuevo estado ID: {}",
                    idSolicitud,
                    idEstado);

                return solicitudUseCase.actualizarSolicitud(idSolicitud, idEstado);
              } catch (NumberFormatException e) {
                log.warn("[ACTUALIZAR_SOLICITUD] Error de formato en IDs: {}", e.getMessage());
                return Mono.error(new ValidationException("Los IDs deben ser números válidos"));
              }
            })
        .doOnSuccess(
            solicitud ->
                log.info(
                    "[ACTUALIZAR_SOLICITUD] Solicitud actualizada exitosamente ID: {} con estado ID: {}",
                    solicitud.getIdSolicitud(),
                    solicitud.getIdEstado()))
        .flatMap(
            solicitudActualizada ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.toResponse(solicitudActualizada), RespuestaSolicitudDTO.class))
        .doOnError(
            ex ->
                log.error(
                    "[ACTUALIZAR_SOLICITUD] Error procesando actualización: {}", ex.getMessage()));
  }
}
