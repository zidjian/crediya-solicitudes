package co.com.crediya.sqs.listener;

import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSProcessor implements Function<Message, Mono<Void>> {
  private final SolicitudUseCase solicitudUseCase;
  private final EstadoRepository estadoRepository;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> apply(Message message) {
    log.info("Mensaje SQS recibido desde capacidad-endeudamiento: {}", message.body());
    return parseMessage(message.body())
        .flatMap(this::processCapacidadResponse)
        .onErrorResume(
            e -> {
              log.error("Error procesando mensaje de capacidad-endeudamiento: {}", e.getMessage(), e);
              return Mono.empty();
            });
  }

  private Mono<CapacidadResponse> parseMessage(String body) {
    return Mono.fromCallable(
        () -> {
          JsonNode jsonNode = objectMapper.readTree(body);
          Long idSolicitud =
              jsonNode.has("idSolicitud") ? jsonNode.get("idSolicitud").asLong() : null;
          String estado = null;
          if (jsonNode.has("status")) {
              estado = jsonNode.get("status").asText();
          }
          String planPago = jsonNode.has("planPago") ? jsonNode.get("planPago").asText() : null;
          log.debug(
              "Mensaje parseado - idSolicitud: {}, estado: {}, planPago: {}",
              idSolicitud,
              estado,
              planPago);
          if (estado == null || estado.isBlank()) {
              log.error("El estado es nulo o vacío en el mensaje original: {}", body);
          }
          return new CapacidadResponse(idSolicitud, estado);
        });
  }

  private Mono<Void> processCapacidadResponse(CapacidadResponse response) {
      if (response == null || response.idSolicitud() == null) {
          log.warn("CapacidadResponse o idSolicitud es nulo: {}", response);
          return Mono.empty();
      }

      String rawEstado = response.estado();
      if (rawEstado == null || rawEstado.isBlank()) {
          log.error("El estado es nulo o vacío para la solicitud {}. Respuesta original: {}", response.idSolicitud(), response);
          return Mono.empty();
      }

      String normalized = rawEstado.trim();
      log.info("Actualizando solicitud {} al estado {}", response.idSolicitud(), normalized);


    return estadoRepository
        .findByNombre(response.estado())
        .switchIfEmpty(Mono.error(new RuntimeException("Estado no encontrado: " + response.estado())))
        .flatMap(
            estado -> {
              log.info("Estado encontrado {} con id {}", estado.getNombre(), estado.getIdEstado());
              return solicitudUseCase.actualizarSolicitud(
                  response.idSolicitud(), estado.getIdEstado(), false);
            })
        .doOnSuccess(
            solicitud -> {
              log.info(
                  "Solicitud {} actualizada exitosamente al estado: {}",
                  response.idSolicitud(),
                  response.estado());
              if ("APROBADO".equalsIgnoreCase(response.estado())) {
                log.info("La solicitud {} ha sido APROBADA", response.idSolicitud());
              } else if ("RECHAZADO".equalsIgnoreCase(response.estado())) {
                log.info("La solicitud {} ha sido RECHAZADA", response.idSolicitud());
              } else if ("PENDIENTE_REVISION".equalsIgnoreCase(response.estado())) {
                log.info("La solicitud {} está PENDIENTE DE REVISIÓN", response.idSolicitud());
              }
            })
        .doOnError(
            e ->
                log.error(
                    "Error al actualizar la solicitud {}: {}", response.idSolicitud(), e.getMessage()))
        .then();
  }

  private record CapacidadResponse(Long idSolicitud, String estado) {}
}
