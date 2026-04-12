package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Category;
import com.example.demo.infra.rest.dto.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper {
    CategoryResponse toResponse(Category category);
}
