package com.example.demo.application.query;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.TypeTransaction;

public record TransactionListFilter(
    Optional<TypeTransaction> tipo,
    Optional<UUID> categoriaId,
    Optional<YearMonth> mes,
    Optional<UUID> titularId
) {

    public static TransactionListFilter none() {
        return new TransactionListFilter(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }
}
