package com.example.demo.infra.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.TitularService;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularRequestMapper;
import com.example.demo.infra.mapper.TitularResponseMapper;
import com.example.demo.infra.rest.dto.TitularRequest;
import com.example.demo.infra.rest.dto.TitularResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/titulars")
public class TitularController {

    private final TitularService titularService;
    private final TitularRequestMapper titularRequestMapper;
    private final TitularResponseMapper titularResponseMapper;

    public TitularController(
        TitularService titularService,
        TitularRequestMapper titularRequestMapper,
        TitularResponseMapper titularResponseMapper
    ) {
        this.titularService = titularService;
        this.titularRequestMapper = titularRequestMapper;
        this.titularResponseMapper = titularResponseMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TitularResponse> getById(@PathVariable UUID id) {
        return titularService.findById(id)
            .map(titularResponseMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TitularResponse> create(@Valid @RequestBody TitularRequest request) {
        Titular created = titularService.createTitular(titularRequestMapper.toDomain(request));
        return new ResponseEntity<>(titularResponseMapper.toResponse(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TitularResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody TitularRequest request
    ) {
        Titular updated = titularService.updateTitular(id, titularRequestMapper.toDomain(request));
        return ResponseEntity.ok(titularResponseMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        titularService.deleteTitular(id);
        return ResponseEntity.noContent().build();
    }
}
