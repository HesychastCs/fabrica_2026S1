package com.example.demo.infra.mapper;

import com.example.demo.domain.model.SavingGoal;
import com.example.demo.infra.persistence.entity.SavingGoalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingGoalEntityMapper {

    @Mapping(source = "titular", target = "titular")
    SavingGoal toDomain(SavingGoalEntity entity);

    @Mapping(source = "titular", target = "titular")
    SavingGoalEntity toEntity(SavingGoal goal);
}