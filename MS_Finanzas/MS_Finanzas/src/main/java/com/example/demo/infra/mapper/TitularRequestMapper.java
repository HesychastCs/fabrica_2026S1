package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Titular;
import com.example.demo.infra.rest.dto.TitularRequest;

@Mapper(componentModel = "spring")
public interface TitularRequestMapper {

    @Mapping(target = "titularId", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "token", ignore = true)
    Titular toDomain(TitularRequest request);
}
