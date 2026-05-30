package com.example.reportes.domain.exception;

public class NoTransactionsInMonthException extends RuntimeException {

    public NoTransactionsInMonthException(Integer mes, Integer anho) {
        super("No hay transacciones registradas para el periodo " + mes + "/" + anho);
    }
}
