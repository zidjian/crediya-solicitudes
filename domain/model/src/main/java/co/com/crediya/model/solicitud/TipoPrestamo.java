package co.com.crediya.model.solicitud;

import java.math.BigDecimal;

public class TipoPrestamo {
  private final Long idTipoPrestamo;
  private final String nombre;
  private final BigDecimal montoMinimo;
  private final BigDecimal montoMaximo;
  private final BigDecimal tasaInteres;
  private final boolean validacionAutomatica;

  public TipoPrestamo(
      Long idTipoPrestamo,
      String nombre,
      BigDecimal montoMinimo,
      BigDecimal montoMaximo,
      BigDecimal tasaInteres,
      boolean validacionAutomatica) {
    if (montoMinimo == null || montoMinimo.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto mínimo debe ser mayor a cero");
    }
    if (montoMaximo == null || montoMaximo.compareTo(montoMinimo) < 0) {
      throw new IllegalArgumentException("El monto máximo debe ser mayor o igual al monto mínimo");
    }
    if (tasaInteres == null || tasaInteres.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("La tasa de interés no puede ser negativa");
    }

    this.idTipoPrestamo = idTipoPrestamo;
    this.nombre = nombre;
    this.montoMinimo = montoMinimo;
    this.montoMaximo = montoMaximo;
    this.tasaInteres = tasaInteres;
    this.validacionAutomatica = validacionAutomatica;
  }

  public static TipoPrestamo toTipoPrestamo(
      Long idTipoPrestamo,
      String nombre,
      BigDecimal montoMinimo,
      BigDecimal montoMaximo,
      BigDecimal tasaInteres,
      boolean validacionAutomatica) {
    return new TipoPrestamo(
        idTipoPrestamo, nombre, montoMinimo, montoMaximo, tasaInteres, validacionAutomatica);
  }

  public boolean validarMonto(BigDecimal monto) {
    if (monto == null) {
      return false;
    }
    return monto.compareTo(montoMinimo) >= 0 && monto.compareTo(montoMaximo) <= 0;
  }

  public Long getIdTipoPrestamo() {
    return idTipoPrestamo;
  }

  public String getNombre() {
    return nombre;
  }

  public BigDecimal getMontoMinimo() {
    return montoMinimo;
  }

  public BigDecimal getMontoMaximo() {
    return montoMaximo;
  }

  public BigDecimal getTasaInteres() {
    return tasaInteres;
  }

  public boolean isValidacionAutomatica() {
    return validacionAutomatica;
  }
}
