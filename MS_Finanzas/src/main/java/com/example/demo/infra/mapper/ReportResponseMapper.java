package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import com.example.demo.domain.model.Report;
import com.example.demo.infra.rest.dto.ReportResponse;

@Mapper(componentModel = "spring")
public interface ReportResponseMapper {
    ReportResponse toResponse(Report report);
}
