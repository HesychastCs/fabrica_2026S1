package com.example.demo.application.service;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.usecase.GetCategoryUseCase;
import com.example.demo.domain.model.Category;

public class CategoryService implements GetCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    
    public CategoryService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }
    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }

}
