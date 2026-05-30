package com.example.demo.infra.mapper;

import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingGoalRequestMapper {

    @Mapping(target = "goalId", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "avance", ignore = true)
    @Mapping(target = "titular", expression = "java(createTitular(request))")
    SavingGoal toDomain(SavingGoalRequest request);

    default Titular createTitular(SavingGoalRequest request) {
        if (request.titularId() == null) {
            throw new IllegalArgumentException(
                "El campo 'titularId' es obligatorio. Debes enviar un UUID válido existente en la base de datos.");
        }
        return new Titular(request.titularId(), null, null, null, null, null, null, null, null);
    }
}