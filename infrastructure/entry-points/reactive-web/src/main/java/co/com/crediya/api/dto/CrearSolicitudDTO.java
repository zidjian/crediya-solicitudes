package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        @Future(message = "El plazo debe ser una fecha futura")
        LocalDate plazo,

        @NotNull(message = "El id del tipo de préstamo es obligatorio")
        @Min(value = 1, message = "El id del tipo de préstamo debe ser mayor a cero")
        Long idTipoPrestamo
) {
}
