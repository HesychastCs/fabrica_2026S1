package com.example.demo.domain.model;

import java.util.List;

public record Category(
    Long id,
    String nombre,
    List<Transaction> transacciones
) {

}
