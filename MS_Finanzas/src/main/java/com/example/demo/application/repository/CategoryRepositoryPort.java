package com.example.demo.application.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Category;

public interface CategoryRepositoryPort {
    Optional<Category> findById(UUID categoryId);
}
