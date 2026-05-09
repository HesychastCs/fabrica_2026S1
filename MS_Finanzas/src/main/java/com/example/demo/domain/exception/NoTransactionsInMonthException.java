package com.example.demo.domain.exception;

public class NoTransactionsInMonthException extends RuntimeException {
    public NoTransactionsInMonthException(Integer mes, Integer anho) {
        super("No hubo movimientos el mes " + mes + " del año " + anho);
    }
}
