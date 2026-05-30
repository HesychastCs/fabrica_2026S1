package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Budget;
import com.example.demo.infra.rest.dto.BudgetRequest;

@Mapper(componentModel = "spring")
public interface BudgetRequestMapper {
    @Mapping(target = "titular.titularId", source= "titularId")
    @Mapping(target = "presupuestoId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Budget toDomain(BudgetRequest budgetRequest); 
}
