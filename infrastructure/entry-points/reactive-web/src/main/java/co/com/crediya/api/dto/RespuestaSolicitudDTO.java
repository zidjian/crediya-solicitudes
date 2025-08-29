package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RespuestaSolicitudDTO(
        Long idSolicitud,
        String documentoIdentidad,
        String email,
        BigDecimal monto,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate plazo,
        String tipoPrestamo,
        String estado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant fechaCreacion,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant fechaActualizacion
) {
}
