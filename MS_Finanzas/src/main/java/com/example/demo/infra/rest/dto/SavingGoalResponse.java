package com.example.demo.infra.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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