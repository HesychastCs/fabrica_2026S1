package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Titular;
import com.example.demo.infra.rest.dto.TitularResponse;

@Mapper(componentModel = "spring")
public interface TitularResponseMapper {

    @Mapping(target = "titularId", source = "titularId")
    TitularResponse toResponse(Titular titular);
}
