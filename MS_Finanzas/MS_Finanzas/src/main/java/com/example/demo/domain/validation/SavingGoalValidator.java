package com.example.demo.domain.validation;

import java.time.LocalDate;

import com.example.demo.domain.model.Titular;

public final class SavingGoalValidator {

    private SavingGoalValidator() {
    }

    public static void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }

    public static void validateMonto(Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    public static void validateFechaLimite(LocalDate fechaLimite) {
        if (fechaLimite != null && !fechaLimite.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura");
        }
    }

    public static void validateTitular(Titular titular) {
        if (titular == null || titular.titularId() == null) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
    }
}
