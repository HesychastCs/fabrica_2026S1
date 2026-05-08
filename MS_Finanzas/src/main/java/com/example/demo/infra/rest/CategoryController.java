package com.example.demo.infra.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.application.service.CategoryService;
import com.example.demo.domain.model.Category;
import com.example.demo.infra.mapper.CategoryRequestMapper;
import com.example.demo.infra.mapper.CategoryResponseMapper;
import com.example.demo.infra.rest.dto.CategoryRequest;
import com.example.demo.infra.rest.dto.CategoryResponse;

import jakarta.validation.Valid;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;
    private final CategoryRequestMapper categoryRequestMapper;

    public CategoryController(CategoryService categoryService, CategoryResponseMapper categoryResponseMapper, CategoryRequestMapper categoryRequestMapper) {
        this.categoryService = categoryService;
        this.categoryResponseMapper = categoryResponseMapper;
        this.categoryRequestMapper = categoryRequestMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {

    return categoryService.findById(id)
            .map(categoryResponseMapper::toResponse)
            .map(category -> new ResponseEntity<>(category, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }   
    
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return new ResponseEntity<>(categories.stream().map(categoryResponseMapper::toResponse).toList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryRequestMapper.toDomain(categoryRequest);
        Category createdCategory = categoryService.addCategory(category);
        return new ResponseEntity<>(categoryResponseMapper.toResponse(createdCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryRequestMapper.toDomain(categoryRequest);
        Category updatedCategory = categoryService.updateCategory(id, category);
        return new ResponseEntity<>(categoryResponseMapper.toResponse(updatedCategory), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
