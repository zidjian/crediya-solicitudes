package co.com.crediya.api.dto;

import java.util.List;

public record PaginatedResponseDTO<T>(
    List<T> contenido, int pagina, int tamanio, long totalElementos, int totalPaginas) {
  public static <T> PaginatedResponseDTO<T> of(
      List<T> contenido, int pagina, int tamanio, long totalElementos) {
    int totalPaginas = (int) Math.ceil((double) totalElementos / tamanio);

    return new PaginatedResponseDTO<>(contenido, pagina, tamanio, totalElementos, totalPaginas);
  }
}
