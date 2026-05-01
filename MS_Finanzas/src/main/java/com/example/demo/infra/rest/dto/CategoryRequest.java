package com.example.demo.infra.rest.dto;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @Valid
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    String nombre,
    UUID titularId
) {
    

}
