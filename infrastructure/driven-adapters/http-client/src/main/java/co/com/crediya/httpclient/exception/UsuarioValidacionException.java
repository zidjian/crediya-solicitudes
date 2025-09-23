package co.com.crediya.httpclient.exception;

public class UsuarioValidacionException extends RuntimeException {

  public UsuarioValidacionException(String message) {
    super(message);
  }

  public UsuarioValidacionException(String message, Throwable cause) {
    super(message, cause);
  }
}
