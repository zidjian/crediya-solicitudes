package co.com.crediya.httpclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioResponseDTO {

    @NotNull
    @JsonProperty("idUsuario")
    private Long idUsuario;

    @NotBlank
    @JsonProperty("nombre")
    private String nombre;

    @NotBlank
    @JsonProperty("apellido")
    private String apellido;

    @Email
    @JsonProperty("email")
    private String email;

    @NotBlank
    @JsonProperty("documentoIdentidad")
    private String documentoIdentidad;

    @JsonProperty("telefono")
    private String telefono;

    @NotNull
    @Positive
    @JsonProperty("idRol")
    private Long idRol;

    @JsonProperty("rol")
    private String rol;

    @JsonProperty("salarioBase")
    private BigDecimal salarioBase;

    @JsonProperty("activo")
    private boolean activo;

    public boolean isValid() {
        return idUsuario != null &&
               nombre != null && !nombre.trim().isEmpty() &&
               apellido != null && !apellido.trim().isEmpty() &&
               documentoIdentidad != null && !documentoIdentidad.trim().isEmpty();
    }
}
