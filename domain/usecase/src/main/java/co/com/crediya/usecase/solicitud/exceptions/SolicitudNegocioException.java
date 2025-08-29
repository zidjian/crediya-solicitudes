package co.com.crediya.usecase.solicitud.exceptions;

public abstract class SolicitudNegocioException extends RuntimeException {
    private final String code;

    protected SolicitudNegocioException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
