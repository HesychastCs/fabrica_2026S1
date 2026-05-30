package com.example.demo.infra.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@CrossOrigin(
        origins = {
                "https://front-end-fe20261.vercel.app",
                "https://front-end-fe20261-c4otfrley-junior-morenos-projects.vercel.app"
        },
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;
    private final CategoryRequestMapper categoryRequestMapper;

    public CategoryController(CategoryService categoryService,
                               CategoryResponseMapper categoryResponseMapper,
                               CategoryRequestMapper categoryRequestMapper) {
        this.categoryService = categoryService;
        this.categoryResponseMapper = categoryResponseMapper;
        this.categoryRequestMapper = categoryRequestMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        return categoryService.findById(id)
                .map(categoryResponseMapper::toResponse)
                .map(response -> EntityModel.of(response,
                        linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel(),
                        linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all")))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponse>>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        List<EntityModel<CategoryResponse>> responses = categories.stream()
                .map(categoryResponseMapper::toResponse)
                .map(response -> EntityModel.of(response,
                        linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all")))
                .toList();
        CollectionModel<EntityModel<CategoryResponse>> collection = CollectionModel.of(responses,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<EntityModel<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryRequestMapper.toDomain(categoryRequest);
        Category createdCategory = categoryService.addCategory(category);
        CategoryResponse response = categoryResponseMapper.toResponse(createdCategory);
        EntityModel<CategoryResponse> model = EntityModel.of(response,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all"));
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryRequestMapper.toDomain(categoryRequest);
        Category updatedCategory = categoryService.updateCategory(id, category);
        CategoryResponse response = categoryResponseMapper.toResponse(updatedCategory);
        EntityModel<CategoryResponse> model = EntityModel.of(response,
                linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all"));
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}