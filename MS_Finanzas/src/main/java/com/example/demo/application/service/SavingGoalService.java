package com.example.demo.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.application.usecase.AddSavingGoalUseCase;
import com.example.demo.application.usecase.RemoveSavingGoalUseCase;
import com.example.demo.application.usecase.RetrieveSavingGoalUseCase;
import com.example.demo.application.usecase.UpdateSavingGoalUseCase;
import com.example.demo.domain.exception.DuplicateGoalNameException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.validation.SavingGoalValidator;

@Service
public class SavingGoalService implements AddSavingGoalUseCase, RetrieveSavingGoalUseCase,
        UpdateSavingGoalUseCase, RemoveSavingGoalUseCase {

    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SavingGoalService(SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    @Override
    public SavingGoal addSavingGoal(SavingGoal savingGoal) {
        if (savingGoal == null) {
            throw new IllegalArgumentException("La meta no puede ser nula");
        }

        SavingGoalValidator.validateNombre(savingGoal.nombre());
        SavingGoalValidator.validateMonto(savingGoal.montoObjetivo());
        SavingGoalValidator.validateFechaLimite(savingGoal.fechaLimite());
        SavingGoalValidator.validateTitular(savingGoal.titular());

        if (savingGoalRepositoryPort.existsByNombre(savingGoal.nombre())) {
            throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + savingGoal.nombre());
        }

        SavingGoal goal = new SavingGoal(
            null,
            savingGoal.nombre(),
            savingGoal.montoObjetivo(),
            0,
            GoalStatus.EN_PROGRESO,
            savingGoal.fechaLimite(),
            savingGoal.titular()
        );
        return savingGoalRepositoryPort.save(goal);
    }

    @Override
    public Optional<SavingGoal> findById(UUID id) {
        return savingGoalRepositoryPort.findById(id);
    }

    @Override
    public List<SavingGoal> findAll() {
        return savingGoalRepositoryPort.findAll();
    }

    @Override
    public SavingGoal updateSavingGoal(UUID goalId, SavingGoal savingGoal) {
        SavingGoal existingGoal = savingGoalRepositoryPort.findById(goalId)
            .orElseThrow(() -> new SavingGoalNotFoundException(
                "Meta de ahorro no encontrada con ID: " + goalId));

        SavingGoalValidator.validateNombre(savingGoal.nombre());
        SavingGoalValidator.validateMonto(savingGoal.montoObjetivo());
        SavingGoalValidator.validateFechaLimite(savingGoal.fechaLimite());

        if (!existingGoal.nombre().equals(savingGoal.nombre())
            && savingGoalRepositoryPort.existsByNombre(savingGoal.nombre())) {
            throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + savingGoal.nombre());
        }

        SavingGoal updatedGoal = new SavingGoal(
            goalId,
            savingGoal.nombre(),
            savingGoal.montoObjetivo(),
            existingGoal.avance(),
            existingGoal.estado(),
            savingGoal.fechaLimite(),
            existingGoal.titular()
        );

        return savingGoalRepositoryPort.update(goalId, updatedGoal);
    }

    @Override
    public void deleteSavingGoalById(UUID goalId) {
        savingGoalRepositoryPort.findById(goalId)
            .orElseThrow(() -> new SavingGoalNotFoundException(
                "Meta de ahorro no encontrada con ID: " + goalId));
        savingGoalRepositoryPort.deleteById(goalId);
    }

    public SavingGoal markAsCompleted(UUID goalId) {
        SavingGoal existing = savingGoalRepositoryPort.findById(goalId)
            .orElseThrow(() -> new SavingGoalNotFoundException(
                "Meta de ahorro no encontrada con ID: " + goalId));
        return savingGoalRepositoryPort.updateAvance(goalId, existing.avance(), GoalStatus.COMPLETADA);
    }
}
