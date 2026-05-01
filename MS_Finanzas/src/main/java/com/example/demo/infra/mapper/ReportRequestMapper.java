package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Report;
import com.example.demo.infra.rest.dto.ReportRequest;

@Mapper(componentModel = "spring")
public interface ReportRequestMapper {
    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "fechaGenerado", ignore = true)
    @Mapping(target = "balanceNeto", ignore=true)
    @Mapping(target = "ingresosAcumulados", ignore=true)
    @Mapping(target = "gastosAcumulados", ignore=true)
    @Mapping(target = "aportesMetaAcumulados", ignore=true)
    @Mapping(target = "titular", ignore=true)
    Report toDomain(ReportRequest reportRequest);
}
