package com.example.demo.application.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Category;

public interface RetrieveCategoryUseCase {
    Optional<Category> findById(UUID categoryId);
    List<Category> findAll();
}
