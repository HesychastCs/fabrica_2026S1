package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.persistence.entity.TitularEntity;

@Mapper(componentModel = "spring")
public interface TitularEntityMapper {
    Titular toDomain(TitularEntity titularEntity);
    TitularEntity toEntity(Titular titular);
}
