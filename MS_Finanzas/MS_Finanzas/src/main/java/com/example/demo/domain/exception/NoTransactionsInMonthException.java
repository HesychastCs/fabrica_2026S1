package com.example.demo.domain.exception;

public class NoTransactionsInMonthException extends RuntimeException {
    public NoTransactionsInMonthException(Integer mes, Integer anho) {
        super(String.format("No hubo movimientos el mes %d del año %d", mes, anho));
    }
}
