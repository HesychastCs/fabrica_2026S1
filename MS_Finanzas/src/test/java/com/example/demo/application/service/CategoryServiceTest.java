package com.example.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.CategoryAlreadyExistsException;
import com.example.demo.domain.exception.CategoryInUseException;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private UUID titularId;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        titularId = UUID.randomUUID();
        category = new Category(categoryId, "Alimentación", new Titular(titularId, "Ana", "Pérez", "Gómez", "3001234567", null, "COP", "America/Bogota", "token"));
    }

    @Test
    void addCategory_shouldSaveWhenNameDoesNotExist() {
        given(categoryRepositoryPort.existsByNameAndTitularId(category.nombre(), titularId)).willReturn(false);
        given(categoryRepositoryPort.save(category)).willReturn(category);

        Category saved = categoryService.addCategory(category);

        assertEquals(category, saved);
    }

    @Test
    void addCategory_shouldThrowWhenCategoryAlreadyExists() {
        given(categoryRepositoryPort.existsByNameAndTitularId(category.nombre(), titularId)).willReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.addCategory(category));
    }

    @Test
    void deleteCategoryById_shouldDeleteWhenNotInUse() {
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.existsByCategoryId(categoryId)).willReturn(false);

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepositoryPort).deleteById(categoryId);
    }

    @Test
    void deleteCategoryById_shouldThrowWhenCategoryIsInUse() {
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.existsByCategoryId(categoryId)).willReturn(true);

        assertThrows(CategoryInUseException.class, () -> categoryService.deleteCategoryById(categoryId));
    }

    @Test
    void updateCategory_shouldThrowWhenCategoryDoesNotExist() {
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, category));
    }
}
