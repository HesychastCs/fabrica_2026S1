package com.example.demo.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.domain.exception.CategoryAlreadyExistsException;
import com.example.demo.domain.exception.CategoryInUseException;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.infra.mapper.CategoryEntityMapper;
import com.example.demo.infra.persistence.entity.CategoryEntity;

@Component
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaTransactionRepository jpaTransactionRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    public JpaCategoryRepositoryAdapter(JpaCategoryRepository jpaCategoryRepository, JpaTransactionRepository jpaTransactionRepository, CategoryEntityMapper categoryEntityMapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.categoryEntityMapper = categoryEntityMapper;
    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        CategoryEntity savedCategoryEntity = jpaCategoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("La categoria no fue encontrada"));
        return Optional.of(categoryEntityMapper.toDomain(savedCategoryEntity));
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll().stream().map(categoryEntityMapper::toDomain).toList();
    }

    @Override
    public Category save(Category category) {
        if (jpaCategoryRepository.existsByNombre(category.nombre())) {
            throw new CategoryAlreadyExistsException(category.nombre());
        }

        CategoryEntity categoryEntity = categoryEntityMapper.toEntity(category);
        CategoryEntity savedCategoryEntity = jpaCategoryRepository.save(categoryEntity);
        return categoryEntityMapper.toDomain(savedCategoryEntity);
    } 

    @Override
    public Category update(UUID categoryId, Category category) {
        if (jpaCategoryRepository.existsByNombre(category.nombre())) {
            throw new CategoryAlreadyExistsException(category.nombre());
        }

        CategoryEntity existingCategoryEntity = jpaCategoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("La categoria no fue encontrada"));
        existingCategoryEntity.setNombre(category.nombre());
        CategoryEntity updatedCategoryEntity = jpaCategoryRepository.save(existingCategoryEntity);
        return categoryEntityMapper.toDomain(updatedCategoryEntity);

    }

    @Override
    public void deleteById(UUID categoryId) {
        if (!jpaCategoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("La categoria no fue encontrada");
        }

        if (jpaTransactionRepository.existsByCategoryId(categoryId)) {
            throw new CategoryInUseException(categoryId.toString());
        }

        jpaCategoryRepository.deleteById(categoryId);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaCategoryRepository.existsByNombre(name);
    }
}