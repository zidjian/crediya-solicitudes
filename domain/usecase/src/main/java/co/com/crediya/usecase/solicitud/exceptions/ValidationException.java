package co.com.crediya.usecase.solicitud.exceptions;

public class ValidationException extends BusinessException {

  public ValidationException(String message) {
    super("VALIDATION_ERROR", message, 400);
  }

  public ValidationException(String message, Throwable cause) {
    super("VALIDATION_ERROR", message, 400, cause);
  }
}
