package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Budget;
import com.example.demo.infra.rest.dto.BudgetResponse;

@Mapper(componentModel = "spring")
public interface BudgetResponseMapper {
    BudgetResponse toResponse(Budget budget);
}
