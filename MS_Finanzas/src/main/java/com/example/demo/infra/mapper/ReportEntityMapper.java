package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Report;
import com.example.demo.infra.persistence.entity.ReportEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;

@Mapper(componentModel = "spring")
public interface ReportEntityMapper {
    @Mapping(target = "titular", source= "titularEntity")
    Report toDomain(ReportEntity reportEntity, TitularEntity titularEntity);
    
    ReportEntity toEntity(Report report);
}
