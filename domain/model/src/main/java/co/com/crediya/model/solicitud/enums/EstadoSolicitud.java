package co.com.crediya.model.solicitud.enums;

public enum EstadoSolicitud {
    PENDIENTE_REVISION("Pendiente de revisión"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
