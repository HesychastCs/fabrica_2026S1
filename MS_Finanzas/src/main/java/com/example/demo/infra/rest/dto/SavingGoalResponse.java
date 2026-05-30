package com.example.demo.infra.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SavingGoalResponse extends RepresentationModel<SavingGoalResponse> {
    private UUID goalId;
    private String nombre;
    private Double montoObjetivo;
    private Double avance;
    private String estado;
    private LocalDate fechaLimite;
    private UUID titularId;
    private String titularNombre;

    @JsonIgnore
    public Double getPorcentajeAvance() {
        if (montoObjetivo == null || montoObjetivo <= 0) return 0.0;
        return (avance / montoObjetivo) * 100;
    }
}