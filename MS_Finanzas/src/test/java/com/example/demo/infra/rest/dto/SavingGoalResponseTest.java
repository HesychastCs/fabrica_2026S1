package com.example.demo.infra.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SavingGoalResponseTest {

    @Test
    void calcularPorcentajeAvance() {
        SavingGoalResponse response = new SavingGoalResponse(
            UUID.randomUUID(),
            "Viaje",
            1000.0,
            250.0,
            "EN_PROGRESO",
            LocalDate.now().plusMonths(3),
            UUID.randomUUID(),
            "Luis"
        );

        assertEquals(25.0, response.getPorcentajeAvance());
    }

    @Test
    void calcularPorcentajeAvance_shouldReturnZeroWhenMontoInvalido() {
        SavingGoalResponse response = new SavingGoalResponse(
            UUID.randomUUID(),
            "Viaje",
            0.0,
            100.0,
            "EN_PROGRESO",
            null,
            UUID.randomUUID(),
            "Luis"
        );

        assertEquals(0.0, response.getPorcentajeAvance());
    }
}
