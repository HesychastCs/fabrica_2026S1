package com.example.demo.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.usecase.AddCategoryUseCase;
import com.example.demo.application.usecase.RemoveCategoryUseCase;
import com.example.demo.application.usecase.RetrieveCategoryUseCase;
import com.example.demo.application.usecase.UpdateCategoryUseCase;
import com.example.demo.domain.model.Category;

@Service
public class CategoryService implements AddCategoryUseCase, RetrieveCategoryUseCase, UpdateCategoryUseCase, RemoveCategoryUseCase {

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
    public Category addCategory(Category category) {
        return categoryRepositoryPort.save(category);
    }

    @Override
    public Category updateCategory(UUID categoryId, Category category) {
        return categoryRepositoryPort.update(categoryId, category);
    }

    @Override
    public void deleteCategoryById(UUID categoryId) {
        categoryRepositoryPort.deleteById(categoryId);
    }

}