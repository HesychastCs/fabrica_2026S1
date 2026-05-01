package com.example.demo.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record SavingGoal(
    UUID goalId,
    String nombre,
    Double montoObjetivo,
    Integer avance,
    GoalStatus estado,
    LocalDate fechaLimite,
    Titular titular
) {}