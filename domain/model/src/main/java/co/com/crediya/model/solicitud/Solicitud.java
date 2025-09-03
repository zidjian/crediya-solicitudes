package co.com.crediya.model.solicitud;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Solicitud {
    private final Long idSolicitud;
    private final String documentoIdentidad;
    private final String email;
    private final BigDecimal monto;
    private final LocalDate plazo;
    private final Long idTipoPrestamo;
    private final Long idEstado;

    private Solicitud(Long idSolicitud, String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo,
                     Long idTipoPrestamo, Long idEstado) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (plazo == null) {
            throw new IllegalArgumentException("El plazo es obligatorio");
        }
        if (idTipoPrestamo == null) {
            throw new IllegalArgumentException("El ID del tipo de préstamo es obligatorio");
        }

        this.idSolicitud = idSolicitud;
        this.documentoIdentidad = documentoIdentidad.trim();
        this.email = email.trim();
        this.monto = monto;
        this.plazo = plazo;
        this.idTipoPrestamo = idTipoPrestamo;
        this.idEstado = idEstado;
    }

    public static Solicitud toSolicitud(String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo,
                                       Long idTipoPrestamo, Long idEstado) {
        return new Solicitud(null, documentoIdentidad, email, monto, plazo, idTipoPrestamo, idEstado);
    }

    public Solicitud cambiarEstado(Long nuevoIdEstado) {
        return new Solicitud(idSolicitud, documentoIdentidad, email, monto, plazo, idTipoPrestamo, nuevoIdEstado);
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getPlazo() {
        return plazo;
    }

    public Long getIdTipoPrestamo() {
        return idTipoPrestamo;
    }

    public Long getIdEstado() {
        return idEstado;
    }
}
