package co.com.crediya.usecase.solicitud.exceptions;

public class TipoPrestamoInvalidoException extends SolicitudNegocioException {
    public TipoPrestamoInvalidoException(String tipoInvalido) {
        super("TIPO_PRESTAMO_INVALIDO",
              String.format("El tipo de préstamo '%s' no es válido. Los tipos válidos son: PERSONAL, HIPOTECARIO, VEHICULAR, COMERCIAL, EDUCATIVO", tipoInvalido));
    }
}
