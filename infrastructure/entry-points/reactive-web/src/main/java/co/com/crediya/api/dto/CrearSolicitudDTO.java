package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

        @NotBlank(message = "El tipo de préstamo es obligatorio")
        String tipoPrestamo
) {
}
