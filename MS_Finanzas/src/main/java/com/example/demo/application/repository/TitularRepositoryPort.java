package com.example.demo.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Titular;

public interface TitularRepositoryPort {

    Optional<Titular> findById(UUID titularId);

    List<Titular> findAll();

    Titular save(Titular titular);

    Titular update(UUID titularId, Titular titular);

    void deleteById(UUID titularId);
}
