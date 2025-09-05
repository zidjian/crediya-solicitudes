package co.com.crediya.model.common;

public record PageRequest(
        int page,
        int size
) {
    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño de la página debe ser positivo");
        }
    }

    public int getOffset() {
        return page * size;
    }
}
