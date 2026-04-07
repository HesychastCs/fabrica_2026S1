package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String email,
    LocalDateTime fechaRegistro,
    String token
) {

}
