package co.com.crediya.usecase.solicitud.exceptions;

public class TechnicalException extends BusinessException {

  public TechnicalException(String message) {
    super("INTERNAL_SERVER_ERROR", message, 500);
  }

  public TechnicalException(String message, Throwable cause) {
    super("INTERNAL_SERVER_ERROR", message, 500, cause);
  }
}
