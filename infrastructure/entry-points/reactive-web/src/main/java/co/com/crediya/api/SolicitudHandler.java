package co.com.crediya.api;

import co.com.crediya.api.dto.CrearSolicitudDTO;
import co.com.crediya.api.mapper.SolicitudDTOMapper;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
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
                .doOnSubscribe(sub -> log.info("[CREAR_SOLICITUD] Petición recibida"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<CrearSolicitudDTO>> violaciones = validator.validate(dto);
                    if (!violaciones.isEmpty()) {
                        log.warn("[CREAR_SOLICITUD] Validación fallida: {} violación(es)", violaciones.size());
                        return Mono.error(new ConstraintViolationException(violaciones));
                    }
                    return Mono.just(dto);
                })
                .flatMap(dto -> solicitudUseCase.crearSolicitud(
                    dto.documentoIdentidad(),
                    dto.email(),
                    dto.monto(),
                    dto.plazo(),
                    dto.idTipoPrestamo()
                ))
                .doOnSuccess(solicitud -> log.info("[CREAR_SOLICITUD] Solicitud creada exitosamente con ID: {} para documento: {}",
                    solicitud.getIdSolicitud(), solicitud.getDocumentoIdentidad()))
                .flatMap(solicitudCreada -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapper.toResponse(solicitudCreada))
                )
                .doOnError(ex -> log.error("[CREAR_SOLICITUD] Error procesando solicitud: {}", ex.getMessage()));
    }
}
