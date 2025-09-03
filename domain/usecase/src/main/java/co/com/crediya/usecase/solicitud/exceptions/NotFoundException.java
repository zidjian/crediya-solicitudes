package co.com.crediya.usecase.solicitud.exceptions;

public class NotFoundException extends BusinessException {

    public NotFoundException() {
        super("NOT_FOUND", "No encontrado", 404);
    }

    public NotFoundException(String mensaje) {
        super("NOT_FOUND", mensaje, 404);
    }

    public NotFoundException(String mensaje, Throwable causa) {
        super("NOT_FOUND", mensaje, 404, causa);
    }
}