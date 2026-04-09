package com.example.demo.domain.model;

import java.util.List;
import java.util.UUID;

public record Category(
    UUID categoriaId,
    String nombre,
    UUID titularId,
    List<Transaction> transacciones
) {

}
