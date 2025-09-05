package co.com.crediya.api;

import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.dto.PaginatedResponseDTO;
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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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
        return serverRequest.bodyToMono(CrearSolicitudDTO.class).doOnSubscribe(sub -> log.info("[CREAR_SOLICITUD] Petición recibida")).flatMap(dto -> {
            Set<ConstraintViolation<CrearSolicitudDTO>> violaciones = validator.validate(dto);
            if (!violaciones.isEmpty()) {
                log.warn("[CREAR_SOLICITUD] Validación fallida: {} violación(es)", violaciones.size());
                return Mono.error(new ConstraintViolationException(violaciones));
            }

            // Validar que el documento del token coincida con el de la petición
            return validateDocumentoOwnership(serverRequest, dto.documentoIdentidad()).then(Mono.just(dto));
        }).flatMap(dto -> solicitudUseCase.crearSolicitud(dto.documentoIdentidad(), dto.email(), dto.monto(), LocalDate.parse(dto.plazo()), dto.idTipoPrestamo())).doOnSuccess(solicitud -> log.info("[CREAR_SOLICITUD] Solicitud creada exitosamente con ID: {} para documento: {}", solicitud.getIdSolicitud(), solicitud.getDocumentoIdentidad())).flatMap(solicitudCreada -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(mapper.toResponse(solicitudCreada))).doOnError(ex -> log.error("[CREAR_SOLICITUD] Error procesando solicitud: {}", ex.getMessage()));
    }

    private Mono<Void> validateDocumentoOwnership(ServerRequest request, String documentoIdentidadPeticion) {
        return authorizationService.extractDocumentoIdentidadFromToken(request).flatMap(documentoIdentidadToken -> {
            if (!documentoIdentidadToken.equals(documentoIdentidadPeticion)) {
                log.warn("[CREAR_SOLICITUD] Documento en token {} no coincide con documento en petición {}", documentoIdentidadToken, documentoIdentidadPeticion);
                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede crear solicitudes para otros usuarios"));
            }
            log.info("[CREAR_SOLICITUD] Validación de documento exitosa: token={}, petición={}", documentoIdentidadToken, documentoIdentidadPeticion);
            return Mono.empty();
        });
    }

    public Mono<ServerResponse> escucharSolicitudesPaginadas(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> {
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

            log.info("[GET_SOLICITUDES_PAGINADAS] Consultando todas las solicitudes, página: {}, tamaño: {}", pagina, tamanio);

            return new int[]{pagina, tamanio};
        })
        .doOnSubscribe(sub -> log.info("[GET_SOLICITUDES_PAGINADAS] Petición recibida"))
        .flatMap(params -> solicitudUseCase.obtenerSolicitudesPaginadas(params[0], params[1]))
        .doOnSuccess(pageResult -> log.info("[GET_SOLICITUDES_PAGINADAS] Se encontraron {} solicitudes de un total de {}", pageResult.content().size(), pageResult.totalElements()))
        .flatMap(pageResult -> {
            // Convertir las solicitudes del dominio a DTOs de respuesta
            var solicitudesDTO = pageResult.content().stream().map(mapper::toResponse).toList();

            // Crear la respuesta paginada
            var paginatedResponse = PaginatedResponseDTO.of(solicitudesDTO, pageResult.page(), pageResult.size(), pageResult.totalElements());

            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(paginatedResponse);
        }).doOnError(ex -> log.error("[GET_SOLICITUDES_PAGINADAS] Error consultando solicitudes: {}", ex.getMessage()));
    }
}
