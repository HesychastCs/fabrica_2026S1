package com.example.demo.infra.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.CategoryService;
import com.example.demo.infra.mapper.CategoryResponseMapper;
import com.example.demo.infra.rest.dto.CategoryResponse;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;

    public CategoryController(CategoryService categoryService, CategoryResponseMapper categoryResponseMapper) {
        this.categoryService = categoryService;
        this.categoryResponseMapper = categoryResponseMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {

    return categoryService.findById(id)
            .map(categoryResponseMapper::toResponse)
            .map(category -> new ResponseEntity<>(category, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }   
    

}
