package com.example.demo.infra.rest;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.application.service.ReportService;
import com.example.demo.domain.model.Report;
import com.example.demo.infra.mapper.ReportRequestMapper;
import com.example.demo.infra.mapper.ReportResponseMapper;
import com.example.demo.infra.rest.dto.ReportRequest;
import com.example.demo.infra.rest.dto.ReportResponse;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@CrossOrigin(
        origins = {
                "https://front-end-fe20261.vercel.app",
                "https://front-end-fe20261-c4otfrley-junior-morenos-projects.vercel.app"
        },
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportResponseMapper reportResponseMapper;
    private final ReportRequestMapper reportRequestMapper;

    public ReportController(ReportService reportService,
                             ReportResponseMapper reportResponseMapper,
                             ReportRequestMapper reportRequestMapper) {
        this.reportService = reportService;
        this.reportResponseMapper = reportResponseMapper;
        this.reportRequestMapper = reportRequestMapper;
    }

    @PostMapping
    public ResponseEntity<EntityModel<ReportResponse>> generateReport(
            @Valid @RequestBody ReportRequest reportRequest) {
        Report report = reportRequestMapper.toDomain(reportRequest);
        Report generatedReport = reportService.generateReport(
                report.mes(), report.anho(), report.titular().titularId());
        ReportResponse response = reportResponseMapper.toResponse(generatedReport);
        EntityModel<ReportResponse> model = EntityModel.of(response,
                linkTo(methodOn(ReportController.class)
                        .generateReport(reportRequest)).withSelfRel());
        return ResponseEntity.ok(model);
    }
}