package com.example.demo.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.domain.model.Category;
import com.example.demo.infra.mapper.CategoryEntityMapper;
import com.example.demo.infra.persistence.entity.CategoryEntity;

@Component
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    public JpaCategoryRepositoryAdapter(JpaCategoryRepository jpaCategoryRepository, CategoryEntityMapper categoryEntityMapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.categoryEntityMapper = categoryEntityMapper;
    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        CategoryEntity savedCategoryEntity = jpaCategoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
        return Optional.of(categoryEntityMapper.toDomainCategory(savedCategoryEntity));
    } 

}
