package com.example.demo.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Titular;

public interface GetTitularUseCase {
    Optional<Titular> findById(UUID titulTitularId);
}
