package co.com.crediya.model.solicitud;

import co.com.crediya.model.solicitud.enums.EstadoSolicitud;
import co.com.crediya.model.solicitud.enums.TipoPrestamo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class Solicitud {
    private final Long idSolicitud;
    private final String documentoIdentidad;
    private final String email;
    private final BigDecimal monto;
    private final LocalDate plazo;
    private final TipoPrestamo tipoPrestamo;
    private final EstadoSolicitud estado;
    private final Instant fechaCreacion;
    private final Instant fechaActualizacion;

    private Solicitud(Long idSolicitud, String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo,
                     TipoPrestamo tipoPrestamo, EstadoSolicitud estado, Instant fechaCreacion, Instant fechaActualizacion) {
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
        if (tipoPrestamo == null) {
            throw new IllegalArgumentException("El tipo de préstamo es obligatorio");
        }

        this.idSolicitud = idSolicitud;
        this.documentoIdentidad = documentoIdentidad.trim();
        this.email = email.trim();
        this.monto = monto;
        this.plazo = plazo;
        this.tipoPrestamo = tipoPrestamo;
        this.estado = estado != null ? estado : EstadoSolicitud.PENDIENTE_REVISION;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : Instant.now();
        this.fechaActualizacion = fechaActualizacion != null ? fechaActualizacion : this.fechaCreacion;
    }

    public static Solicitud crear(String documentoIdentidad, String email, BigDecimal monto, LocalDate plazo, TipoPrestamo tipoPrestamo) {
        return new Solicitud(null, documentoIdentidad, email, monto, plazo, tipoPrestamo,
                           EstadoSolicitud.PENDIENTE_REVISION, null, null);
    }

    public Solicitud conId(Long idSolicitud) {
        return new Solicitud(idSolicitud, documentoIdentidad, email, monto, plazo, tipoPrestamo,
                           estado, fechaCreacion, fechaActualizacion);
    }

    public Solicitud cambiarEstado(EstadoSolicitud nuevoEstado) {
        return new Solicitud(idSolicitud, documentoIdentidad, email, monto, plazo, tipoPrestamo,
                           nuevoEstado, fechaCreacion, Instant.now());
    }

    // Getters
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

    public TipoPrestamo getTipoPrestamo() {
        return tipoPrestamo;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}
