package com.example.demo.domain.model;

import java.time.LocalDateTime;

public record Transaccion(
    Long id,
    String nombre,
    String monto,
    String descripcion,
    TipoTransaccion tipoTransaccion,
    LocalDateTime fechaTransaccion
) {

}
