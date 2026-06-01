package com.example.demo.application.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.usecase.CreateTitularUseCase;
import com.example.demo.application.usecase.DeleteTitularUseCase;
import com.example.demo.application.usecase.GetTitularUseCase;
import com.example.demo.application.usecase.UpdateTitularUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Titular;

@Service
public class TitularService implements
    GetTitularUseCase,
    CreateTitularUseCase,
    UpdateTitularUseCase,
    DeleteTitularUseCase {

    private final TitularRepositoryPort titularRepositoryPort;

    public TitularService(TitularRepositoryPort titularRepositoryPort) {
        this.titularRepositoryPort = titularRepositoryPort;
    }

    @Override
    public Optional<Titular> findById(UUID id) {
        return titularRepositoryPort.findById(id);
    }

    @Override
    public Titular createTitular(Titular titular) {
        if (titular == null || titular.nombre() == null || titular.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        Instant fechaRegistro = titular.fechaRegistro() != null ? titular.fechaRegistro() : Instant.now();

        Titular toSave = new Titular(
            null,
            titular.nombre(),
            titular.primerApellido(),
            titular.segundoApellido(),
            titular.telefono(),
            fechaRegistro,
            titular.monedaPreferida(),
            titular.zonaHoraria(),
            null
        );
        return titularRepositoryPort.save(toSave);
    }

    @Override
    public Titular updateTitular(UUID id, Titular titular) {
        Titular existing = titularRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));

        Titular updated = new Titular(
            id,
            titular.nombre() != null ? titular.nombre() : existing.nombre(),
            titular.primerApellido() != null ? titular.primerApellido() : existing.primerApellido(),
            titular.segundoApellido() != null ? titular.segundoApellido() : existing.segundoApellido(),
            titular.telefono() != null ? titular.telefono() : existing.telefono(),
            existing.fechaRegistro(),
            titular.monedaPreferida() != null ? titular.monedaPreferida() : existing.monedaPreferida(),
            titular.zonaHoraria() != null ? titular.zonaHoraria() : existing.zonaHoraria(),
            existing.token()
        );
        return titularRepositoryPort.update(id, updated);
    }

    @Override
    public void deleteTitular(UUID id) {
        titularRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));
        titularRepositoryPort.deleteById(id);
    }
}
