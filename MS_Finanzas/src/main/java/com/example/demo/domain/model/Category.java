package com.example.demo.domain.model;

import java.util.UUID;

public record Category(
    UUID categoriaId,
    String nombre,
    Titular titular
) {

}
