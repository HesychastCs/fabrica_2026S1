package com.example.demo.infra.mapper;

import com.example.demo.domain.model.SavingGoal;
import com.example.demo.infra.rest.dto.SavingGoalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingGoalResponseMapper {

    @Mapping(target = "avance", source = "avance")
    @Mapping(target = "estado", expression = "java(goal.estado().name())")
    @Mapping(target = "titularId", source = "goal.titular.titularId")
    @Mapping(target = "titularNombre", source = "goal.titular.nombre")
    SavingGoalResponse toResponse(SavingGoal goal);
}