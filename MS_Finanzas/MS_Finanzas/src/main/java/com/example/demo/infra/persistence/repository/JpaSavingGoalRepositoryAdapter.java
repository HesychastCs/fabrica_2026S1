package com.example.demo.infra.persistence.repository;

import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.infra.mapper.SavingGoalEntityMapper;
import com.example.demo.infra.persistence.entity.SavingGoalEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSavingGoalRepositoryAdapter implements SavingGoalRepositoryPort {

    private final JpaSavingGoalRepository jpaSavingGoalRepository;
    private final JpaTitularRepository jpaTitularRepository;
    private final SavingGoalEntityMapper mapper;

    public JpaSavingGoalRepositoryAdapter(
            JpaSavingGoalRepository jpaSavingGoalRepository,
            JpaTitularRepository jpaTitularRepository,
            SavingGoalEntityMapper mapper) {
        this.jpaSavingGoalRepository = jpaSavingGoalRepository;
        this.jpaTitularRepository = jpaTitularRepository;
        this.mapper = mapper;
    }

    @Override
    public SavingGoal save(SavingGoal savingGoal) {
        SavingGoalEntity entity = mapper.toEntity(savingGoal);
        TitularEntity titularEntity = jpaTitularRepository
                .findById(savingGoal.titular().titularId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Titular no encontrado con id: " + savingGoal.titular().titularId()));
        entity.setTitular(titularEntity);
        return mapper.toDomain(jpaSavingGoalRepository.save(entity));
    }

    @Override
    public Optional<SavingGoal> findById(UUID id) {
        return jpaSavingGoalRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SavingGoal> findAll() {
        return jpaSavingGoalRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public SavingGoal update(UUID id, SavingGoal savingGoal) {
        SavingGoalEntity entity = jpaSavingGoalRepository.findById(id)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + id));
        entity.setNombre(savingGoal.nombre());
        entity.setMontoObjetivo(savingGoal.montoObjetivo());
        entity.setFechaLimite(savingGoal.fechaLimite());
        entity.setEstado(savingGoal.estado());
        entity.setAvance(savingGoal.avance());
        return mapper.toDomain(jpaSavingGoalRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaSavingGoalRepository.deleteById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaSavingGoalRepository.existsByNombre(nombre);
    }

    @Override
    public SavingGoal updateAvance(UUID goalId, int nuevoAvance, GoalStatus nuevoEstado) {
        SavingGoalEntity entity = jpaSavingGoalRepository.findById(goalId)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + goalId));
        entity.setAvance(nuevoAvance);
        entity.setEstado(nuevoEstado);
        return mapper.toDomain(jpaSavingGoalRepository.save(entity));
    }
}