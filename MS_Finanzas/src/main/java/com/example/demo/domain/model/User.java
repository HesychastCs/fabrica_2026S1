package com.example.demo.domain.model;

import java.time.LocalDateTime;

public record User(
    Long id,
    String email,
    String nombre_completo,
    LocalDateTime fechaDeRegistro,
    String token
) {

}
