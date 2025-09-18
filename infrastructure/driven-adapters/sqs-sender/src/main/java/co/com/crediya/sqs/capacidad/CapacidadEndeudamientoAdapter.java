package co.com.crediya.sqs.capacidad;

import co.com.crediya.model.solicitud.Estado;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.TipoPrestamo;
import co.com.crediya.model.solicitud.gateways.CapacidadEndeudamientoGateway;
import co.com.crediya.model.solicitud.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.gateways.TipoPrestamoRepository;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.sqs.sender.SQSSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CapacidadEndeudamientoAdapter implements CapacidadEndeudamientoGateway {

    private final SQSSender sqsSender;
    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    @Override
    public Mono<String> enviarSolicitudCapacidadEndeudamiento(Usuario usuario, Solicitud solicitud, List<Solicitud> solicitudes, Long idSolicitudActual) {
        return crearMensajeCapacidadEndeudamientoReactive(usuario, solicitudes, idSolicitudActual)
                .doOnNext(mensaje -> log.info("[SQS_CAPACIDAD] Enviando solicitud de capacidad de endeudamiento para usuario: {} y solicitud ID: {}",
                    usuario.getDocumentoIdentidad(), idSolicitudActual))
                .flatMap(mensaje -> sqsSender.send(mensaje, "https://sqs.us-east-1.amazonaws.com/920004595108/cola-capacidad-endeudamiento.fifo"))
                .doOnSuccess(messageId -> log.info("[SQS_CAPACIDAD] Solicitud enviada exitosamente. MessageId: {} para solicitud ID: {}",
                    messageId, idSolicitudActual))
                .doOnError(ex -> log.error("[SQS_CAPACIDAD] Error enviando solicitud para ID: {} - {}",
                    idSolicitudActual, ex.getMessage()));
    }

    private Mono<String> crearMensajeCapacidadEndeudamientoReactive(Usuario usuario, List<Solicitud> solicitudes, Long idSolicitudActual) {
        return Flux.fromIterable(solicitudes)
                .flatMap(solicitud -> Mono.zip(
                        tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo()),
                        estadoRepository.findById(solicitud.getIdEstado())
                ).map(tuple -> {
                    TipoPrestamo tipoPrestamo = tuple.getT1();
                    Estado estado = tuple.getT2();
                    return String.format("""
                        {
                            "id": %d,
                            "idUser": "%s",
                            "email": "%s",
                            "monto": %.2f,
                            "plazo": "%s",
                            "tipoPrestamo": "%s",
                            "estado": "%s",
                            "tasaInteres": %.2f,
                            "deudaTotalMensual": %.2f
                        }""",
                        solicitud.getIdSolicitud(),
                        escapeJson(solicitud.getIdUser()),
                        escapeJson(solicitud.getEmail()),
                        solicitud.getMonto().doubleValue(),
                        solicitud.getPlazo().toString(),
                        escapeJson(tipoPrestamo.getNombre()),
                        escapeJson(estado.getNombre()),
                        tipoPrestamo.getTasaInteres().doubleValue(),
                        solicitud.calcularDeudaTotalMensual(tipoPrestamo.getTasaInteres()).doubleValue()
                    );
                }))
                .collectList()
                .map(solicitudesJsonList -> {
                    StringBuilder solicitudesJson = new StringBuilder("[");
                    for (int i = 0; i < solicitudesJsonList.size(); i++) {
                        solicitudesJson.append(solicitudesJsonList.get(i));
                        if (i < solicitudesJsonList.size() - 1) {
                            solicitudesJson.append(",");
                        }
                    }
                    solicitudesJson.append("]");

                    return String.format("""
                        {
                            "datosUsuario": {
                                "idUsuario": %d,
                                "nombre": "%s",
                                "apellido": "%s",
                                "email": "%s",
                                "documentoIdentidad": "%s",
                                "telefono": "%s",
                                "rol": "%s",
                                "salarioBase": %.2f
                            },
                            "datosSolicitudes": %s,
                            "idSolicitud": %d
                        }
                        """,
                        usuario.getIdUsuario(),
                        escapeJson(usuario.getNombre()),
                        escapeJson(usuario.getApellido()),
                        escapeJson(usuario.getEmail()),
                        escapeJson(usuario.getDocumentoIdentidad()),
                        escapeJson(usuario.getTelefono()),
                        escapeJson(usuario.getRol()),
                        usuario.getSalarioBase() != null ? usuario.getSalarioBase().doubleValue() : 0.0,
                        solicitudesJson.toString(),
                        idSolicitudActual
                    );
                });
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}