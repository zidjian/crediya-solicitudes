package co.com.crediya.model.usuario;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Usuario {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Long idRol;
    private boolean activo;
}
