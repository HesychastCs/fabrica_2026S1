package com.example.demo.application.service;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.usecase.GetTitularUseCase;
import com.example.demo.domain.model.Titular;

public class TitularService implements GetTitularUseCase {

    private final TitularRepositoryPort titularRepositoryPort;
    
    public TitularService(TitularRepositoryPort titularRepositoryPort) {
        this.titularRepositoryPort = titularRepositoryPort;
    }
    
    @Override
    public Optional<Titular> findById(UUID id) {
        return titularRepositoryPort.findById(id);
    }

}
