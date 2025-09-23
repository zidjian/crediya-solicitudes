package co.com.crediya.model.solicitud;

public class Estado {
  private final Long idEstado;
  private final String nombre;
  private final String descripcion;

  public Estado(Long idEstado, String nombre, String descripcion) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del estado es obligatorio");
    }

    this.idEstado = idEstado;
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  public static Estado toEstado(Long idEstado, String nombre, String descripcion) {
    return new Estado(idEstado, nombre, descripcion);
  }

  public Long getIdEstado() {
    return idEstado;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
