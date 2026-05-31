package com.example.demo.infra.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.application.service.SavingGoalService;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.infra.mapper.SavingGoalRequestMapper;
import com.example.demo.infra.mapper.SavingGoalResponseMapper;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import com.example.demo.infra.rest.dto.SavingGoalResponse;

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
            .map(this::addLinks)
            .map(goal -> new ResponseEntity<>(goal, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<SavingGoalResponse>> getAllSavingGoals() {
        List<SavingGoal> goals = savingGoalService.findAll();
        List<SavingGoalResponse> responses = goals.stream()
                .map(savingGoalResponseMapper::toResponse)
                .map(this::addLinks)
                .toList();
        CollectionModel<SavingGoalResponse> collection = CollectionModel.of(responses,
                linkTo(methodOn(SavingGoalController.class).getAllSavingGoals()).withSelfRel());
        return new ResponseEntity<>(collection, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SavingGoalResponse> createSavingGoal(
            @Valid @RequestBody SavingGoalRequest request) {
        SavingGoal goal = savingGoalRequestMapper.toDomain(request);
        SavingGoal createdGoal = savingGoalService.addSavingGoal(goal);
        SavingGoalResponse response = addLinks(savingGoalResponseMapper.toResponse(createdGoal));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> updateSavingGoal(
            @PathVariable UUID id,
            @Valid @RequestBody SavingGoalRequest request) {
        SavingGoal goal = savingGoalRequestMapper.toDomain(request);
        SavingGoal updatedGoal = savingGoalService.updateSavingGoal(id, goal);
        SavingGoalResponse response = addLinks(savingGoalResponseMapper.toResponse(updatedGoal));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavingGoal(@PathVariable UUID id) {
        savingGoalService.deleteSavingGoalById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private SavingGoalResponse addLinks(SavingGoalResponse response) {
        UUID id = response.getGoalId();
        if (id != null) {
            response.add(linkTo(methodOn(SavingGoalController.class)
                    .getSavingGoalById(id)).withSelfRel());
            response.add(linkTo(methodOn(SavingGoalController.class)
                    .updateSavingGoal(id, null)).withRel("update"));
            response.add(linkTo(methodOn(SavingGoalController.class)
                    .deleteSavingGoal(id)).withRel("delete"));
            response.add(linkTo(methodOn(SavingGoalController.class)
                    .getAllSavingGoals()).withRel("all"));
        }
        return response;
    }
}