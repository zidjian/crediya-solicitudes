package co.com.crediya.model.solicitud.enums;

public enum TipoPrestamo {
    PERSONAL("Préstamo Personal"),
    HIPOTECARIO("Préstamo Hipotecario"),
    VEHICULAR("Préstamo Vehicular"),
    COMERCIAL("Préstamo Comercial"),
    EDUCATIVO("Préstamo Educativo");

    private final String descripcion;

    TipoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static boolean esValido(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return false;
        }
        try {
            TipoPrestamo.valueOf(tipo.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
