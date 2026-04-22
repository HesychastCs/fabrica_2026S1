package com.example.demo.infra.rest.dto;

import java.util.UUID;

public record CategoryRequest(
    String nombre,
    UUID titularId
) {
    

}
