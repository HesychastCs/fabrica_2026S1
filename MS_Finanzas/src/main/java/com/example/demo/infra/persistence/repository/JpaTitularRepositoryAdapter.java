package com.example.demo.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularEntityMapper;
import com.example.demo.infra.persistence.entity.TitularEntity;

@Component
public class JpaTitularRepositoryAdapter implements TitularRepositoryPort {

    private final JpaTitularRepository jpaTitularRepository;
    private final TitularEntityMapper titularEntityMapper;

    public JpaTitularRepositoryAdapter(
        JpaTitularRepository jpaTitularRepository,
        TitularEntityMapper titularEntityMapper
    ) {
        this.jpaTitularRepository = jpaTitularRepository;
        this.titularEntityMapper = titularEntityMapper;
    }

    @Override
    public Optional<Titular> findById(UUID id) {
        return jpaTitularRepository.findById(id)
            .map(titularEntityMapper::toDomain);
    }

    @Override
    public List<Titular> findAll() {
        return jpaTitularRepository.findAll().stream()
            .map(titularEntityMapper::toDomain)
            .toList();
    }

    @Override
    public Titular save(Titular titular) {
        TitularEntity entity = titularEntityMapper.toEntity(titular);
        return titularEntityMapper.toDomain(jpaTitularRepository.save(entity));
    }

    @Override
    public Titular update(UUID titularId, Titular titular) {
        TitularEntity entity = jpaTitularRepository.findById(titularId)
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));
        entity.setNombre(titular.nombre());
        entity.setPrimerApellido(titular.primerApellido());
        entity.setSegundoApellido(titular.segundoApellido());
        entity.setTelefono(titular.telefono());
        entity.setMonedaPreferida(titular.monedaPreferida());
        entity.setZonaHoraria(titular.zonaHoraria());
        return titularEntityMapper.toDomain(jpaTitularRepository.save(entity));
    }

    @Override
    public void deleteById(UUID titularId) {
        if (!jpaTitularRepository.existsById(titularId)) {
            throw new ResourceNotFoundException("Titular no encontrado");
        }
        jpaTitularRepository.deleteById(titularId);
    }
}
