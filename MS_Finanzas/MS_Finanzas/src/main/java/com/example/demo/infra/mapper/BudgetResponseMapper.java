package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Budget;
import com.example.demo.infra.rest.dto.BudgetResponse;

@Mapper(componentModel = "spring")
public interface BudgetResponseMapper {
    @Mapping(target = "nombreTitular", source = "titular.nombre")
    BudgetResponse toResponse(Budget budget);
}
