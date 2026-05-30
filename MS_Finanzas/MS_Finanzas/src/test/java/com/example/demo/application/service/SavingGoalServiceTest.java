package com.example.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.domain.exception.DuplicateGoalNameException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;

@ExtendWith(MockitoExtension.class)
class SavingGoalServiceTest {

    @Mock
    private SavingGoalRepositoryPort savingGoalRepositoryPort;

    @InjectMocks
    private SavingGoalService savingGoalService;

    private UUID goalId;
    private UUID titularId;
    private Titular titular;
    private SavingGoal existingGoal;
    private SavingGoal newGoalPayload;

    @BeforeEach
    void setUp() {
        goalId = UUID.randomUUID();
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Luis", "Pérez", "Gómez", "3001112233", null, "COP", "America/Bogota", "token");
        existingGoal = new SavingGoal(
            goalId,
            "Viaje",
            1_000_000.0,
            250_000,
            GoalStatus.EN_PROGRESO,
            LocalDate.now().plusMonths(6),
            titular
        );
        newGoalPayload = new SavingGoal(
            null,
            "Viaje actualizado",
            1_200_000.0,
            0,
            GoalStatus.EN_PROGRESO,
            LocalDate.now().plusMonths(8),
            titular
        );
    }

    @Test
    void actualizarMetaAhorroExitosa() {
        given(savingGoalRepositoryPort.findById(goalId)).willReturn(Optional.of(existingGoal));
        given(savingGoalRepositoryPort.existsByNombre("Viaje actualizado")).willReturn(false);
        given(savingGoalRepositoryPort.update(eq(goalId), any(SavingGoal.class))).willAnswer(inv -> inv.getArgument(1));

        SavingGoal result = savingGoalService.updateSavingGoal(goalId, newGoalPayload);

        assertEquals("Viaje actualizado", result.nombre());
        assertEquals(1_200_000.0, result.montoObjetivo());
        verify(savingGoalRepositoryPort).update(eq(goalId), any(SavingGoal.class));
    }

    @Test
    void eliminarMetaAhorroExitosa() {
        given(savingGoalRepositoryPort.findById(goalId)).willReturn(Optional.of(existingGoal));

        savingGoalService.deleteSavingGoalById(goalId);

        verify(savingGoalRepositoryPort).deleteById(goalId);
    }

    @Test
    void marcarMetaComoCompletada() {
        SavingGoal completed = new SavingGoal(
            goalId,
            existingGoal.nombre(),
            existingGoal.montoObjetivo(),
            existingGoal.avance(),
            GoalStatus.COMPLETADA,
            existingGoal.fechaLimite(),
            titular
        );
        given(savingGoalRepositoryPort.findById(goalId)).willReturn(Optional.of(existingGoal));
        given(savingGoalRepositoryPort.updateAvance(goalId, existingGoal.avance(), GoalStatus.COMPLETADA))
            .willReturn(completed);

        SavingGoal result = savingGoalService.markAsCompleted(goalId);

        assertEquals(GoalStatus.COMPLETADA, result.estado());
        verify(savingGoalRepositoryPort).updateAvance(goalId, existingGoal.avance(), GoalStatus.COMPLETADA);
    }

    @Test
    void rechazarMontoNegativo() {
        SavingGoal invalid = new SavingGoal(null, "Meta", 0.0, 0, GoalStatus.EN_PROGRESO, null, titular);

        assertThrows(IllegalArgumentException.class, () -> savingGoalService.addSavingGoal(invalid));
    }

    @Test
    void rechazarFechaPasada() {
        SavingGoal invalid = new SavingGoal(
            null,
            "Meta",
            100.0,
            0,
            GoalStatus.EN_PROGRESO,
            LocalDate.now().minusDays(1),
            titular
        );

        assertThrows(IllegalArgumentException.class, () -> savingGoalService.addSavingGoal(invalid));
    }

    @Test
    void detectarDuplicados() {
        SavingGoal toCreate = new SavingGoal(
            null,
            "Viaje",
            500.0,
            0,
            GoalStatus.EN_PROGRESO,
            LocalDate.now().plusMonths(1),
            titular
        );
        given(savingGoalRepositoryPort.existsByNombre("Viaje")).willReturn(true);

        assertThrows(DuplicateGoalNameException.class, () -> savingGoalService.addSavingGoal(toCreate));
    }

    @Test
    void updateSavingGoal_shouldThrowWhenNotFound() {
        given(savingGoalRepositoryPort.findById(goalId)).willReturn(Optional.empty());

        assertThrows(SavingGoalNotFoundException.class,
            () -> savingGoalService.updateSavingGoal(goalId, newGoalPayload));
    }
}
