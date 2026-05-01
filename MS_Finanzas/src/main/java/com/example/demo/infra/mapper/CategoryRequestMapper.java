package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Category;
import com.example.demo.infra.rest.dto.CategoryRequest;

@Mapper(componentModel = "spring")
public interface CategoryRequestMapper {

    @Mapping(target = "titular.titularId", source= "titularId")
    @Mapping(target = "categoriaId", ignore = true)
    Category toDomain(CategoryRequest categoryRequest); 
}
