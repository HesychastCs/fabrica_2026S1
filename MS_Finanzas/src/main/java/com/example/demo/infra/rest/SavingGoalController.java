package com.example.demo.infra.rest;

import java.util.List;
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

import com.example.demo.application.service.SavingGoalService;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.infra.mapper.SavingGoalRequestMapper;
import com.example.demo.infra.mapper.SavingGoalResponseMapper;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import com.example.demo.infra.rest.dto.SavingGoalResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/saving-goals")
public class SavingGoalController {

    private final SavingGoalService savingGoalService;
    private final SavingGoalResponseMapper savingGoalResponseMapper;
    private final SavingGoalRequestMapper savingGoalRequestMapper;

    public SavingGoalController(SavingGoalService savingGoalService,
                                SavingGoalResponseMapper savingGoalResponseMapper,
                                SavingGoalRequestMapper savingGoalRequestMapper) {
        this.savingGoalService = savingGoalService;
        this.savingGoalResponseMapper = savingGoalResponseMapper;
        this.savingGoalRequestMapper = savingGoalRequestMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> getSavingGoalById(@PathVariable UUID id) {
        return savingGoalService.findById(id)
            .map(savingGoalResponseMapper::toResponse)
            .map(goal -> new ResponseEntity<>(goal, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<SavingGoalResponse>> getAllSavingGoals() {
        List<SavingGoal> goals = savingGoalService.findAll();
        return new ResponseEntity<>(
            goals.stream().map(savingGoalResponseMapper::toResponse).toList(),
            HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SavingGoalResponse> createSavingGoal(
            @Valid @RequestBody SavingGoalRequest request) {
        SavingGoal goal = savingGoalRequestMapper.toDomain(request);
        SavingGoal createdGoal = savingGoalService.addSavingGoal(goal);
        return new ResponseEntity<>(savingGoalResponseMapper.toResponse(createdGoal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> updateSavingGoal(
            @PathVariable UUID id,
            @Valid @RequestBody SavingGoalRequest request) {
        SavingGoal goal = savingGoalRequestMapper.toDomain(request);
        SavingGoal updatedGoal = savingGoalService.updateSavingGoal(id, goal);
        return new ResponseEntity<>(savingGoalResponseMapper.toResponse(updatedGoal), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavingGoal(@PathVariable UUID id) {
        savingGoalService.deleteSavingGoalById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}