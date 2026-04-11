package com.example.demo.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Category;

public interface GetCategoryUseCase {
    Optional<Category> findById(UUID categoryId);

}
