package com.example.demo.application.usecase;

import java.util.UUID;

import com.example.demo.domain.model.Category;

public interface UpdateCategoryUseCase {
    Category updateCategory(UUID categoryId, Category category);
}
