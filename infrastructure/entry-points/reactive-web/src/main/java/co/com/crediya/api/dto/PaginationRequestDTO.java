package co.com.crediya.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaginationRequestDTO(
    @NotNull(message = "El número de página es obligatorio")
        @Min(value = 0, message = "El número de página debe ser mayor o igual a 0")
        Integer pagina,
    @NotNull(message = "El tamaño de página es obligatorio")
        @Min(value = 1, message = "El tamaño de página debe ser mayor a 0")
        Integer tamanio) {}
