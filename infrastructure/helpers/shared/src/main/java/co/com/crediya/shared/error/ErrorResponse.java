package co.com.crediya.shared.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final String codigo;
  private final String mensaje;
  private final int estado;
  private final String ruta;
  private final Instant fechaHora;
  private final String idCorrelacion;
  private final List<ErrorDetail> detalles;

  public ErrorResponse(
      String codigo,
      String mensaje,
      int estado,
      String ruta,
      Instant fechaHora,
      String idCorrelacion,
      List<ErrorDetail> detalles) {
    this.codigo = codigo;
    this.mensaje = mensaje;
    this.estado = estado;
    this.ruta = ruta;
    this.fechaHora = fechaHora;
    this.idCorrelacion = idCorrelacion;
    this.detalles = detalles;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public int getEstado() {
    return estado;
  }

  public String getRuta() {
    return ruta;
  }

  public Instant getFechaHora() {
    return fechaHora;
  }

  public String getIdCorrelacion() {
    return idCorrelacion;
  }

  public List<ErrorDetail> getDetalles() {
    return detalles;
  }

  public static ErrorResponse of(
      String codigo,
      String mensaje,
      int estado,
      String ruta,
      List<ErrorDetail> detalles,
      String idCorrelacion) {
    return new ErrorResponse(codigo, mensaje, estado, ruta, Instant.now(), idCorrelacion, detalles);
  }
}
