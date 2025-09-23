package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RespuestaSolicitudDTO(
    Long id,
    String idUser,
    String email,
    BigDecimal monto,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate plazo,
    String tipoPrestamo,
    String estado,
    BigDecimal tasaInteres,
    BigDecimal deudaTotalMensual) {}
