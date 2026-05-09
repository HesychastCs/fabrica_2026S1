package com.example.demo.infra.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.service.CategoryService;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.CategoryRequestMapper;
import com.example.demo.infra.mapper.CategoryResponseMapper;
import com.example.demo.infra.rest.dto.CategoryRequest;
import com.example.demo.infra.rest.dto.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CategoryControllerTest {

    private final StubCategoryService categoryService = new StubCategoryService();

    private CategoryController controller;
    private CategoryResponseMapper categoryResponseMapper;
    private CategoryRequestMapper categoryRequestMapper;

    private UUID titularId;
    private UUID categoryId;
    private Category category;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Salud", new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", java.time.Instant.now(), "COP", "America/Bogota", "token-1"));
        response = new CategoryResponse(category.nombre());
        categoryResponseMapper = value -> response;
        categoryRequestMapper = request -> category;
        categoryService.findByIdResult = Optional.of(category);
        categoryService.findAllResult = List.of(category);
        categoryService.addResult = category;
        categoryService.updateResult = category;
        controller = new CategoryController(categoryService, categoryResponseMapper, categoryRequestMapper);
    }

    @Test
    void getCategoryById_shouldReturnOkWhenFound() {
        ResponseEntity<CategoryResponse> result = controller.getCategoryById(categoryId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getCategoryById_shouldReturnNotFoundWhenMissing() {
        categoryService.findByIdResult = Optional.empty();

        ResponseEntity<CategoryResponse> result = controller.getCategoryById(UUID.randomUUID());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllCategories_shouldReturnOkAndMappedResponses() {
        ResponseEntity<List<CategoryResponse>> result = controller.getAllCategories();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void createCategory_shouldReturnCreatedResponse() {
        CategoryRequest request = new CategoryRequest("Salud", titularId);

        ResponseEntity<CategoryResponse> result = controller.createCategory(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(categoryService.lastAddedCategory).isEqualTo(category);
    }

    @Test
    void deleteCategory_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteCategory(categoryId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(categoryService.lastDeletedCategoryId).isEqualTo(categoryId);
    }

    private static final class StubCategoryService extends CategoryService {

        private Optional<Category> findByIdResult = Optional.empty();
        private List<Category> findAllResult = List.of();
        private Category addResult;
        private Category updateResult;
        private Category lastAddedCategory;
        private UUID lastDeletedCategoryId;

        private StubCategoryService() {
            super(null, null);
        }

        @Override
        public Optional<Category> findById(UUID id) {
            return findByIdResult;
        }

        @Override
        public List<Category> findAll() {
            return findAllResult;
        }

        @Override
        public Category addCategory(Category category) {
            lastAddedCategory = category;
            return addResult;
        }

        @Override
        public Category updateCategory(UUID categoryId, Category category) {
            return updateResult;
        }

        @Override
        public void deleteCategoryById(UUID categoryId) {
            lastDeletedCategoryId = categoryId;
        }
    }
}
