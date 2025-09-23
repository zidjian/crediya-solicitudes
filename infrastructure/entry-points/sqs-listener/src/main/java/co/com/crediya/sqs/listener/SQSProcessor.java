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
    log.info("Received SQS message from capacidad-endeudamiento: {}", message.body());
    return parseMessage(message.body())
        .flatMap(this::processCapacidadResponse)
        .onErrorResume(
            e -> {
              log.error("Error processing capacidad-endeudamiento message: {}", e.getMessage(), e);
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
          if (jsonNode.has("estado")) {
              estado = jsonNode.get("estado").asText();
          } else if (jsonNode.has("status")) {
              estado = jsonNode.get("status").asText();
              log.warn("Campo 'estado' no encontrado, usando 'status': {}", estado);
          }
          String planPago = jsonNode.has("planPago") ? jsonNode.get("planPago").asText() : null;
          log.debug(
              "Parsed message - idSolicitud: {}, estado: {}, planPago: {}",
              idSolicitud,
              estado,
              planPago);
          if (estado == null || estado.isBlank()) {
              log.error("Estado is null or blank in raw message: {}", body);
          }
          return new CapacidadResponse(idSolicitud, estado);
        });
  }

  private Mono<Void> processCapacidadResponse(CapacidadResponse response) {
      if (response == null || response.idSolicitud() == null) {
          log.warn("CapacidadResponse or idSolicitud is null: {}", response);
          return Mono.empty();
      }

      String rawEstado = response.estado();
      if (rawEstado == null || rawEstado.isBlank()) {
          log.error("Estado is null or blank for solicitud {}. Raw response: {}", response.idSolicitud(), response);
          return Mono.empty();
      }

      String normalized = rawEstado.trim();
      log.info("Updating solicitud {} to estado {}", response.idSolicitud(), normalized);


    return estadoRepository
        .findByNombre(response.estado())
        .switchIfEmpty(Mono.error(new RuntimeException("Estado not found: " + response.estado())))
        .flatMap(
            estado -> {
              log.info("Found estado {} with id {}", estado.getNombre(), estado.getIdEstado());
              return solicitudUseCase.actualizarSolicitud(
                  response.idSolicitud(), estado.getIdEstado());
            })
        .doOnSuccess(
            solicitud -> {
              log.info(
                  "Successfully updated solicitud {} to estado: {}",
                  response.idSolicitud(),
                  response.estado());
              if ("APROBADO".equalsIgnoreCase(response.estado())) {
                log.info("Solicitud {} has been APPROVED", response.idSolicitud());
              } else if ("RECHAZADO".equalsIgnoreCase(response.estado())) {
                log.info("Solicitud {} has been REJECTED", response.idSolicitud());
              } else if ("PENDIENTE_REVISION".equalsIgnoreCase(response.estado())) {
                log.info("Solicitud {} is PENDING REVIEW", response.idSolicitud());
              }
            })
        .doOnError(
            e ->
                log.error(
                    "Failed to update solicitud {}: {}", response.idSolicitud(), e.getMessage()))
        .then();
  }

  private record CapacidadResponse(Long idSolicitud, String estado) {}
}
