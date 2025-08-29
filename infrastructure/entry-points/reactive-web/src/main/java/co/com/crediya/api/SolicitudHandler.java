package co.com.crediya.api;

import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.mapper.SolicitudDTOMapper;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import co.com.crediya.usecase.solicitud.exceptions.SolicitudNegocioException;
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
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolicitudHandler {
    private final SolicitudUseCase solicitudUseCase;
    private final SolicitudDTOMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> escucharCrearSolicitud(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(CrearSolicitudDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El cuerpo de la petición es requerido")))
                .doOnSubscribe(sub -> log.info("[CREAR_SOLICITUD] Petición recibida"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<CrearSolicitudDTO>> violaciones = validator.validate(dto);
                    if (!violaciones.isEmpty()) {
                        log.warn("[CREAR_SOLICITUD] Validación fallida: {} violación(es)", violaciones.size());
                        violaciones.forEach(v -> log.warn("[CREAR_SOLICITUD] Violación: {} - {}", v.getPropertyPath(), v.getMessage()));
                        return Mono.error(new ConstraintViolationException(violaciones));
                    }
                    return Mono.just(dto);
                })
                .doOnNext(dto -> log.info("[CREAR_SOLICITUD] Procesando solicitud para documento: {}, email: {}, monto: {}, tipo: {}",
                    dto.documentoIdentidad(), dto.email(), dto.monto(), dto.tipoPrestamo()))
                .flatMap(dto -> solicitudUseCase.crearSolicitud(
                    dto.documentoIdentidad(),
                    dto.email(),
                    dto.monto(),
                    dto.plazo(),
                    dto.tipoPrestamo()
                ))
                .doOnSuccess(solicitud -> log.info("[CREAR_SOLICITUD] Solicitud creada exitosamente con ID: {} para documento: {}",
                    solicitud.getIdSolicitud(), solicitud.getDocumentoIdentidad()))
                .flatMap(solicitudCreada -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapper.toResponse(solicitudCreada))
                )
                .onErrorResume(SolicitudNegocioException.class, ex -> {
                    log.warn("[CREAR_SOLICITUD] Error de negocio: {} - {}", ex.getCode(), ex.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                "error", ex.getCode(),
                                "message", ex.getMessage(),
                                "timestamp", System.currentTimeMillis()
                            ));
                })
                .onErrorResume(ConstraintViolationException.class, ex -> {
                    log.warn("[CREAR_SOLICITUD] Error de validación: {}", ex.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                "error", "VALIDATION_ERROR",
                                "message", "Errores de validación en los datos enviados",
                                "timestamp", System.currentTimeMillis()
                            ));
                })
                .onErrorResume(IllegalArgumentException.class, ex -> {
                    log.warn("[CREAR_SOLICITUD] Argumento inválido: {}", ex.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                "error", "INVALID_ARGUMENT",
                                "message", ex.getMessage(),
                                "timestamp", System.currentTimeMillis()
                            ));
                })
                .doOnError(ex -> log.error("[CREAR_SOLICITUD] Error procesando solicitud: {}", ex.getMessage(), ex));
    }
}
