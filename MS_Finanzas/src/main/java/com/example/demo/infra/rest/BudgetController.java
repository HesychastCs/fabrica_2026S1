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

import com.example.demo.application.service.BudgetService;
import com.example.demo.domain.model.Budget;
import com.example.demo.infra.mapper.BudgetRequestMapper;
import com.example.demo.infra.mapper.BudgetResponseMapper;
import com.example.demo.infra.rest.dto.BudgetRequest;
import com.example.demo.infra.rest.dto.BudgetResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final BudgetResponseMapper budgetResponseMapper;
    private final BudgetRequestMapper budgetRequestMapper;

    public BudgetController(BudgetService budgetService, BudgetResponseMapper budgetResponseMapper,
            BudgetRequestMapper budgetRequestMapper) {
        this.budgetService = budgetService;
        this.budgetResponseMapper = budgetResponseMapper;
        this.budgetRequestMapper = budgetRequestMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable UUID id) {

    return budgetService.findById(id)
            .map(budgetResponseMapper::toResponse)
            .map(budget -> new ResponseEntity<>(budget, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }   

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        List<Budget> budgets = budgetService.findAll();
        return new ResponseEntity<>(budgets.stream().map(budgetResponseMapper::toResponse).toList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest budgetRequest) {
        Budget budget = budgetRequestMapper.toDomain(budgetRequest);
        Budget createdBudget = budgetService.addBudget(budget, budget.fechaInicio(), budget.fechaFinal());
        return new ResponseEntity<>(budgetResponseMapper.toResponse(createdBudget), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable UUID id, @Valid @RequestBody BudgetRequest budgetRequest) {
        Budget budget = budgetRequestMapper.toDomain(budgetRequest);
        Budget updatedBudget = budgetService.updateBudget(id, budget);
        return new ResponseEntity<>(budgetResponseMapper.toResponse(updatedBudget), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
