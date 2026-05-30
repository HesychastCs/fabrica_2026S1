package com.example.demo.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

@Service
public class SavingGoalService implements AddSavingGoalUseCase, RetrieveSavingGoalUseCase,
        UpdateSavingGoalUseCase, RemoveSavingGoalUseCase {

    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SavingGoalService(SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    @Override
    public SavingGoal addSavingGoal(SavingGoal savingGoal) {
        validateNewGoal(savingGoal);
        SavingGoal goal = new SavingGoal(
                null,
                savingGoal.nombre(),
                savingGoal.montoObjetivo(),
                0,
                GoalStatus.EN_PROGRESO,
                savingGoal.fechaLimite(),
                savingGoal.titular());
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

        validateGoalPayload(savingGoal);
        validateUniqueNameOnUpdate(existingGoal, savingGoal.nombre());

        SavingGoal updatedGoal = new SavingGoal(
                goalId,
                savingGoal.nombre(),
                savingGoal.montoObjetivo(),
                existingGoal.avance(),
                existingGoal.estado(),
                savingGoal.fechaLimite(),
                existingGoal.titular());

        return savingGoalRepositoryPort.update(goalId, updatedGoal);
    }

    @Override
    public void deleteSavingGoalById(UUID goalId) {
        savingGoalRepositoryPort.findById(goalId)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + goalId));
        savingGoalRepositoryPort.deleteById(goalId);
    }

    private void validateNewGoal(SavingGoal savingGoal) {
        Objects.requireNonNull(savingGoal, "La meta no puede ser nula");
        validateGoalPayload(savingGoal);
        requireTitular(savingGoal);
        validateUniqueName(savingGoal.nombre());
    }

    private void requireTitular(SavingGoal savingGoal) {
        if (savingGoal.titular() == null || savingGoal.titular().titularId() == null) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
    }

    private void validateGoalPayload(SavingGoal savingGoal) {
        requireNombre(savingGoal.nombre());
        requireMontoValido(savingGoal.montoObjetivo());
        validateFutureDate(savingGoal.fechaLimite());
    }

    private void requireNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }

    private void requireMontoValido(Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    private void validateUniqueNameOnUpdate(SavingGoal existingGoal, String newName) {
        if (!existingGoal.nombre().equals(newName)) {
            validateUniqueName(newName);
        }
    }

    private void validateUniqueName(String name) {
        if (savingGoalRepositoryPort.existsByNombre(name)) {
            throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + name);
        }
    }

    private void validateFutureDate(LocalDate fechaLimite) {
        if (fechaLimite != null && !fechaLimite.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura");
        }
    }
}
