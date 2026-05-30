package com.example.demo.application.usecase;

import java.util.UUID;

import com.example.demo.domain.model.Titular;

public interface UpdateTitularUseCase {

    Titular updateTitular(UUID id, Titular titular);
}
