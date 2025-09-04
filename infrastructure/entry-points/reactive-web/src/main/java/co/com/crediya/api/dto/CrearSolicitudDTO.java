package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record CrearSolicitudDTO(
        @NotBlank(message = "El documento de identidad es obligatorio")
        String documentoIdentidad,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "1", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotNull(message = "El plazo es obligatorio")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "El plazo debe tener el formato YYYY-MM-DD")
        String plazo,

        @NotNull(message = "El id del tipo de préstamo es obligatorio")
        @Min(value = 1, message = "El id del tipo de préstamo debe ser mayor a cero")
        Long idTipoPrestamo
) {
}
