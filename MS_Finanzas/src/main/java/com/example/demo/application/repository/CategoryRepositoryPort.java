package com.example.demo.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Category;

public interface CategoryRepositoryPort {
    
    List<Category> findAll();
    Optional<Category> findById(UUID categoryId);
    Category save(Category category);
    Category update(UUID categoryId, Category category);
    void deleteById(UUID categoryId);

    Optional<Category> findByNombreIgnoreCase(String nombre);
    boolean existsByNameAndTitularId(String name, UUID titularId);
}
