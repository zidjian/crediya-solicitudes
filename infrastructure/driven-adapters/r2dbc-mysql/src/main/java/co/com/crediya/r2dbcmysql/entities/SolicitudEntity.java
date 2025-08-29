package co.com.crediya.r2dbcmysql.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Table("solicitud")
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private Long idSolicitud;

    private BigDecimal monto;

    private LocalDate plazo;

    private String email;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("id_estado")
    private Long idEstado;

    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

    @Column("fecha_creacion")
    private Instant fechaCreacion;

    @Column("fecha_actualizacion")
    private Instant fechaActualizacion;

    public SolicitudEntity() {
    }

    public SolicitudEntity(Long idSolicitud, BigDecimal monto, LocalDate plazo, String email, String documentoIdentidad,
                          Long idEstado, Long idTipoPrestamo, Instant fechaCreacion, Instant fechaActualizacion) {
        this.idSolicitud = idSolicitud;
        this.monto = monto;
        this.plazo = plazo;
        this.email = email;
        this.documentoIdentidad = documentoIdentidad;
        this.idEstado = idEstado;
        this.idTipoPrestamo = idTipoPrestamo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y setters
    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getPlazo() {
        return plazo;
    }

    public void setPlazo(LocalDate plazo) {
        this.plazo = plazo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public Long getIdTipoPrestamo() {
        return idTipoPrestamo;
    }

    public void setIdTipoPrestamo(Long idTipoPrestamo) {
        this.idTipoPrestamo = idTipoPrestamo;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
