package com.example.demo.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.AddCategoryUseCase;
import com.example.demo.application.usecase.RemoveCategoryUseCase;
import com.example.demo.application.usecase.RetrieveCategoryUseCase;
import com.example.demo.application.usecase.UpdateCategoryUseCase;
import com.example.demo.domain.exception.CategoryAlreadyExistsException;
import com.example.demo.domain.exception.CategoryInUseException;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;

@Service
public class CategoryService implements AddCategoryUseCase, RetrieveCategoryUseCase, UpdateCategoryUseCase, RemoveCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    
    public CategoryService(CategoryRepositoryPort categoryRepositoryPort, TransactionRepositoryPort transactionRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
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
        if (categoryRepositoryPort.existsByNameAndTitularId(category.nombre(), category.titular().titularId())) {
            throw new CategoryAlreadyExistsException(category.nombre());
        }
        return categoryRepositoryPort.save(category);
    }

    @Override
    public Category updateCategory(UUID categoryId, Category category) {
        categoryRepositoryPort.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoria no fue encontrada"));
        
        if (categoryRepositoryPort.existsByNameAndTitularId(category.nombre(), category.titular().titularId())) {
            throw new CategoryAlreadyExistsException(category.nombre());
        }
        
        return categoryRepositoryPort.update(categoryId, category);
    }

    @Override
    public void deleteCategoryById(UUID categoryId) {
        categoryRepositoryPort.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoria no fue encontrada"));
        
        if (transactionRepositoryPort.existsByCategoryId(categoryId)) {
            throw new CategoryInUseException(categoryId.toString());
        }
        
        categoryRepositoryPort.deleteById(categoryId);
    }

}