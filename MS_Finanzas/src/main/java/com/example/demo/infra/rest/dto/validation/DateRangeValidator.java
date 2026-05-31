package com.example.demo.infra.rest.dto.validation;

import com.example.demo.infra.rest.dto.BudgetRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, BudgetRequest> {

    @Override
    public boolean isValid(BudgetRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }
        
        // fechaFinal must be equal or after fechaInicio
        return !value.fechaFinal().isBefore(value.fechaInicio());
    }
}