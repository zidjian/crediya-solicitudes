package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActualizarSolicitudDTO(
    @NotNull(message = "El id de solicitud es obligatorio") Long idSolicitud,
    @NotNull(message = "El estado es obligatorio") Long idEstado) {}
