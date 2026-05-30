package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Budget;
import com.example.demo.infra.persistence.entity.BudgetEntity;

@Mapper(componentModel = "spring")
public interface BudgetEntityMapper {
    Budget toDomain(BudgetEntity budgetEntity);
    BudgetEntity toEntity(Budget budget);
}
