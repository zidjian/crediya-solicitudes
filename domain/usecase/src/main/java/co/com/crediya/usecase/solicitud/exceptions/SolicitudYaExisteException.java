package co.com.crediya.usecase.solicitud.exceptions;

public class SolicitudYaExisteException extends SolicitudNegocioException {
    private final String documentoIdentidad;

    public SolicitudYaExisteException(String documentoIdentidad) {
        super("SOLICITUD_YA_EXISTE", "Ya existe una solicitud activa para el documento de identidad: " + documentoIdentidad);
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }
}
