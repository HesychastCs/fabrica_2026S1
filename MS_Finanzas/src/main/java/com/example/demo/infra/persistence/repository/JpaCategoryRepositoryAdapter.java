package com.example.demo.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
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
        return jpaCategoryRepository.findById(categoryId)
            .map(categoryEntityMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll().stream().map(categoryEntityMapper::toDomain).toList();
    }

    @Override
    public Category save(Category category) {
        CategoryEntity categoryEntity = categoryEntityMapper.toEntity(category);
        CategoryEntity savedCategoryEntity = jpaCategoryRepository.save(categoryEntity);
        return categoryEntityMapper.toDomain(savedCategoryEntity);
    } 

    @Override
    public Category update(UUID categoryId, Category category) {
        CategoryEntity existingCategoryEntity = jpaCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoria no fue encontrada"));
        existingCategoryEntity.setNombre(category.nombre());
        CategoryEntity updatedCategoryEntity = jpaCategoryRepository.save(existingCategoryEntity);
        return categoryEntityMapper.toDomain(updatedCategoryEntity);
    }

    @Override
    public void deleteById(UUID categoryId) {
        jpaCategoryRepository.deleteById(categoryId);
    }

    @Override
    public boolean existsByNameAndTitularId(String name, UUID titularId) {
        return jpaCategoryRepository.existsByNombreIgnoreCaseAndTitular_TitularId(name, titularId);
    }

    @Override
    public Optional<Category> findByNombreIgnoreCase(String nombre) {
        return jpaCategoryRepository.findByNombreIgnoreCase(nombre)
            .map(categoryEntityMapper::toDomain);
    }

}