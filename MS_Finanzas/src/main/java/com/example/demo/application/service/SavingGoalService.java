package com.example.demo.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SavingGoalService.class);

    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SavingGoalService(SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    @Override
    public SavingGoal addSavingGoal(SavingGoal savingGoal) {
        log.info("=== VALIDACIONES DE SERVICIO ===");

        if (savingGoal == null)
            throw new IllegalArgumentException("La meta no puede ser nula");

        if (savingGoal.nombre() == null || savingGoal.nombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (savingGoal.montoObjetivo() == null || savingGoal.montoObjetivo() <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");

        if (savingGoal.fechaLimite() != null && !savingGoal.fechaLimite().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura");

        if (savingGoal.titular() == null || savingGoal.titular().titularId() == null)
            throw new IllegalArgumentException("El titular es obligatorio");

        if (savingGoalRepositoryPort.existsByNombre(savingGoal.nombre()))
            throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + savingGoal.nombre());

        log.info("=== TODAS LAS VALIDACIONES PASARON ===");

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
        log.info("=== UPDATE SAVING GOAL ===");

        SavingGoal existingGoal = savingGoalRepositoryPort.findById(goalId)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + goalId));

        if (savingGoal.nombre() == null || savingGoal.nombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (!existingGoal.nombre().equals(savingGoal.nombre())) {
            if (savingGoalRepositoryPort.existsByNombre(savingGoal.nombre()))
                throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + savingGoal.nombre());
        }

        if (savingGoal.montoObjetivo() == null || savingGoal.montoObjetivo() <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");

        if (savingGoal.fechaLimite() != null && !savingGoal.fechaLimite().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura");

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
}