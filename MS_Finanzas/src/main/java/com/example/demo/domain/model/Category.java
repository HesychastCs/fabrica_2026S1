package com.example.demo.domain.model;

import java.util.List;
import java.util.UUID;

public record Category(
    UUID id,
    String nombre,
    TypeCategory tipo,
    List<Transaction> transacciones
) {

}
