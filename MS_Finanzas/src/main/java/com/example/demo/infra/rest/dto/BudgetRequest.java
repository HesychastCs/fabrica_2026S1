package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.infra.rest.dto.validation.ValidDateRange;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@ValidDateRange
public record BudgetRequest(
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0")
    BigDecimal montoLimite,
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    LocalDate fechaInicio,

    @NotNull(message = "La fecha final es obligatoria")
    LocalDate fechaFinal,

    @NotBlank(message = "El titular es obligatorio")
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "El titularId debe ser un UUID válido")
    String titularId
) {

}
