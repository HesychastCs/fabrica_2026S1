package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Category;
import com.example.demo.infra.persistence.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryEntityMapper {
    Category toDomain(CategoryEntity categoryEntity);

    @Mapping(target = "transacciones", ignore = true)
    CategoryEntity toEntity(Category category);
}
