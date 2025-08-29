package co.com.crediya.usecase.solicitud.exceptions;

import co.com.crediya.model.solicitud.enums.TipoPrestamo;

import java.math.BigDecimal;

public class MontoInvalidoException extends RuntimeException {

    public MontoInvalidoException(BigDecimal monto, TipoPrestamo tipoPrestamo,
                                 BigDecimal montoMinimo, BigDecimal montoMaximo) {
        super(String.format("El monto %s no es válido para el tipo de préstamo %s. " +
                           "Debe estar entre %s y %s",
                           monto, tipoPrestamo.getDescripcion(), montoMinimo, montoMaximo));
    }
}
