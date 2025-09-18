package co.com.crediya.model.usuario;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

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
    private String rol;
    private BigDecimal salarioBase;
    private boolean activo;
}
