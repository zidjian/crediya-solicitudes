package co.com.crediya.shared.error;

public class ErrorDetail {
    private final String campo;
    private final String mensaje;

    public ErrorDetail(String campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public String getCampo() { return campo; }
    public String getMensaje() { return mensaje; }
}
