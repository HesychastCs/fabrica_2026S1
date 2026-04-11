package com.example.demo.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularEntityMapper;
import com.example.demo.infra.persistence.entity.TitularEntity;

@Component
public class JpaTitularRepositoryAdapter implements TitularRepositoryPort {
    private final JpaTitularRepository jpaTitularRepository;
    private final TitularEntityMapper titularEntityMapper;

    public JpaTitularRepositoryAdapter(JpaTitularRepository jpaTitularRepository, TitularEntityMapper titularEntityMapper) {
        this.jpaTitularRepository = jpaTitularRepository;
        this.titularEntityMapper = titularEntityMapper;
    }

    @Override
    public Optional<Titular> findById(UUID id) {
        TitularEntity savedTitularEntity = jpaTitularRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Titular no encontrado"));
        return Optional.of(titularEntityMapper.toDomainTitular(savedTitularEntity));
    }
}
