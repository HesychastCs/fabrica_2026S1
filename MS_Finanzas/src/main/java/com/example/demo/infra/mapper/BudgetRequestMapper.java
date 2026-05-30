package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.demo.domain.model.Budget;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.rest.dto.BudgetRequest;

@Mapper(componentModel = "spring")
public interface BudgetRequestMapper {
    @Mapping(target = "titular", source = "budgetRequest", qualifiedByName = "toTitular")
    @Mapping(target = "presupuestoId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "gastoAcumulado", ignore = true)
    @Mapping(target = "montoDisponible", ignore = true)
    Budget toDomain(BudgetRequest budgetRequest); 

    @Named("toTitular")
    default Titular toTitular(BudgetRequest budgetRequest) {
        return new Titular(
            UUID.fromString(budgetRequest.titularId()),
            null, null, null, null, null, null, null, null
        );
    }
}
