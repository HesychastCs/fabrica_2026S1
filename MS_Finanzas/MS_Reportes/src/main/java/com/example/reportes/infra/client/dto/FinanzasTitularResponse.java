package com.example.reportes.infra.client.dto;

import java.util.UUID;

public record FinanzasTitularResponse(
    UUID titularId,
    String nombre,
    String monedaPreferida
) {
}
