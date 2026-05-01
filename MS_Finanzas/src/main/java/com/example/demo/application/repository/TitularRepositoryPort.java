package com.example.demo.application.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Titular;

public interface TitularRepositoryPort {
    Optional<Titular> findById(UUID titularId);
}
