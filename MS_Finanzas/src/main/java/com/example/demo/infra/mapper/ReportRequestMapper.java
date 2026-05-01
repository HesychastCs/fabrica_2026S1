package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.rest.dto.ReportRequest;

@Mapper(componentModel = "spring")
public interface ReportRequestMapper {
    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "fechaGenerado", ignore = true)
    @Mapping(target = "balanceNeto", ignore=true)
    @Mapping(target = "ingresosAcumulados", ignore=true)
    @Mapping(target = "gastosAcumulados", ignore=true)
    @Mapping(target = "aportesMetaAcumulados", ignore=true)
    @Mapping(target = "titular", source="titularId")
    Report toDomain(ReportRequest reportRequest);

    default Titular mapTitularId(UUID titularId) {
        if (titularId == null) {
            return null;
        }
        // Create a minimal Titular with just the ID (other fields will be loaded from DB)
        return new Titular(titularId, null, null, null, null, null, null, null, null);
    }
}
