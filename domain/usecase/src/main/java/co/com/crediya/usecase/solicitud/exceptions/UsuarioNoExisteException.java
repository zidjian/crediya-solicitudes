package co.com.crediya.usecase.solicitud.exceptions;

import co.com.crediya.usecase.solicitud.exceptions.SolicitudNegocioException;

public class UsuarioNoExisteException extends SolicitudNegocioException {
    public UsuarioNoExisteException(String documentoIdentidad) {
        super("USUARIO_NO_EXISTE", "El usuario con documento " + documentoIdentidad + " no existe en el sistema");
    }
}
