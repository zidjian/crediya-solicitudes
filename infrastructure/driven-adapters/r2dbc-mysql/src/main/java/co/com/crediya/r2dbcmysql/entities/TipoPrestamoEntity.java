package co.com.crediya.r2dbcmysql.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

    private String nombre;

    @Column("monto_minimo")
    private BigDecimal montoMinimo;

    @Column("monto_maximo")
    private BigDecimal montoMaximo;

    @Column("tasa_interes")
    private BigDecimal tasaInteres;

    @Column("validacion_automatica")
    private Boolean validacionAutomatica;

    public TipoPrestamoEntity() {
    }

    public TipoPrestamoEntity(Long idTipoPrestamo, String nombre, BigDecimal montoMinimo,
                             BigDecimal montoMaximo, BigDecimal tasaInteres, Boolean validacionAutomatica) {
        this.idTipoPrestamo = idTipoPrestamo;
        this.nombre = nombre;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.tasaInteres = tasaInteres;
        this.validacionAutomatica = validacionAutomatica;
    }

    // Getters y Setters
    public Long getIdTipoPrestamo() {
        return idTipoPrestamo;
    }

    public void setIdTipoPrestamo(Long idTipoPrestamo) {
        this.idTipoPrestamo = idTipoPrestamo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(BigDecimal montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public BigDecimal getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(BigDecimal tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public Boolean getValidacionAutomatica() {
        return validacionAutomatica;
    }

    public void setValidacionAutomatica(Boolean validacionAutomatica) {
        this.validacionAutomatica = validacionAutomatica;
    }
}
