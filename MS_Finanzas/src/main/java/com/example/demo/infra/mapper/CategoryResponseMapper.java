package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Category;
import com.example.demo.infra.rest.dto.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper {
    CategoryResponse toResponse(Category category);

    @Mapping(target = "categoriaId", ignore = true)
    @Mapping(target = "titular", ignore = true)
    Category toDomain(CategoryResponse categoryResponse);
}
