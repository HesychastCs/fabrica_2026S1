package com.example.demo.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.usecase.CreateCategoryUseCase;
import com.example.demo.application.usecase.GetCategoryUseCase;
import com.example.demo.domain.model.Category;

@Service
public class CategoryService implements GetCategoryUseCase, CreateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    
    public CategoryService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }
    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }
    @Override
    public List<Category> findAll() {
        return categoryRepositoryPort.findAll();
    }
    @Override
    public Category createCategory(Category category) {
        return categoryRepositoryPort.save(category);
    }

}
