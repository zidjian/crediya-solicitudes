package co.com.crediya.httpclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private String documentoIdentidad;
    private String nombre;
    private String email;
    private boolean activo;
}
