package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Category;
import com.example.demo.infra.persistence.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryEntityMapper {
    Category toDomainCategory(CategoryEntity categoryEntity);

    @Mapping(target = "categoriaId", ignore = true)
    @Mapping(target = "transacciones", ignore = true)
    CategoryEntity toEntityCategory(Category category);
}
