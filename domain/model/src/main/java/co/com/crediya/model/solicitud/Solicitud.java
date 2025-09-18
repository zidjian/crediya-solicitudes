package co.com.crediya.model.solicitud;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Solicitud {
    private final Long idSolicitud;
    private final String idUser;
    private final String email;
    private final BigDecimal monto;
    private final LocalDate plazo;
    private final Long idTipoPrestamo;
    private final Long idEstado;

    private Solicitud(Long idSolicitud, String idUser, String email, BigDecimal monto, LocalDate plazo,
                      Long idTipoPrestamo, Long idEstado) {
        if (idUser == null || idUser.isBlank()) {
            throw new IllegalArgumentException("El idUser es obligatorio");
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
        this.idUser = idUser.trim();
        this.email = email.trim();
        this.monto = monto;
        this.plazo = plazo;
        this.idTipoPrestamo = idTipoPrestamo;
        this.idEstado = idEstado;
    }

    public static Solicitud toSolicitud(String idUser, String email, BigDecimal monto, LocalDate plazo,
                                        Long idTipoPrestamo, Long idEstado) {
        return new Solicitud(null, idUser, email, monto, plazo, idTipoPrestamo, idEstado);
    }

    public static Solicitud fromDatabase(Long idSolicitud, String idUser, String email, BigDecimal monto,
                                        LocalDate plazo, Long idTipoPrestamo, Long idEstado) {
        return new Solicitud(idSolicitud, idUser, email, monto, plazo, idTipoPrestamo, idEstado);
    }

    public Solicitud cambiarEstado(Long nuevoIdEstado) {
        return new Solicitud(idSolicitud, idUser, email, monto, plazo, idTipoPrestamo, nuevoIdEstado);
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public String getIdUser() {
        return idUser;
    }

    @Deprecated
    public String getDocumentoIdentidad() {
        return getIdUser();
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

    /**
     * Calcula la cuota mensual (deuda total mensual) usando la fórmula de anualidad financiera:
     *   PMT = P * [r (1 + r)^n] / [(1 + r)^n - 1]
     * donde:
     *   P = monto del préstamo
     *   r = tasa de interés mensual (tasa anual / 12 / 100)
     *   n = número de meses entre la fecha actual y la fecha de plazo
     *
     * Casos especiales:
     * - Si n <= 0, se considera n = 1 para evitar divisiones por cero.
     * - Si r = 0 (tasa de interés anual = 0), la cuota es P / n.
     *
     * @param tasaInteresAnual tasa de interés anual expresada en porcentaje (p.ej. 5.5 para 5.5%)
     * @return cuota mensual redondeada a 2 decimales
     */
    public BigDecimal calcularDeudaTotalMensual(BigDecimal tasaInteresAnual) {
        long mesesPlazo = ChronoUnit.MONTHS.between(LocalDate.now(), this.plazo);
        if (mesesPlazo <= 0) {
            mesesPlazo = 1;
        }

        BigDecimal tasaInteresMensual = tasaInteresAnual
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        if (tasaInteresMensual.compareTo(BigDecimal.ZERO) == 0) {
            return this.monto.divide(BigDecimal.valueOf(mesesPlazo), 2, RoundingMode.HALF_UP);
        }

        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaInteresMensual);
        BigDecimal unoPlusTasaPotencia = unoPlusTasa.pow((int) mesesPlazo);

        BigDecimal numerador = this.monto.multiply(tasaInteresMensual).multiply(unoPlusTasaPotencia);
        BigDecimal denominador = unoPlusTasaPotencia.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
}
