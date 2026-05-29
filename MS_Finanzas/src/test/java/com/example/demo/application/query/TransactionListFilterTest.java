package com.example.demo.application.query;

import com.example.demo.domain.model.TypeTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransactionListFilter")
class TransactionListFilterTest {

    @Test
    @DisplayName("none() retorna filtro completamente vacío")
    void none_returnsEmptyFilter() {
        TransactionListFilter filter = TransactionListFilter.none();

        assertThat(filter.tipo()).isEmpty();
        assertThat(filter.categoriaId()).isEmpty();
        assertThat(filter.mes()).isEmpty();
    }

    @Test
    @DisplayName("constructor con valores retorna opcionales presentes")
    void constructor_withValues_returnsPresentOptionals() {
        UUID categoriaId = UUID.randomUUID();
        YearMonth mes = YearMonth.of(2025, 5);

        TransactionListFilter filter = new TransactionListFilter(
                Optional.of(TypeTransaction.GASTO),
                Optional.of(categoriaId),
                Optional.of(mes)
        );

        assertThat(filter.tipo()).contains(TypeTransaction.GASTO);
        assertThat(filter.categoriaId()).contains(categoriaId);
        assertThat(filter.mes()).contains(mes);
    }

    @Test
    @DisplayName("constructor con vacíos equivale a none()")
    void constructor_withEmptyOptionals_equalsNone() {
        TransactionListFilter manual = new TransactionListFilter(
                Optional.empty(), Optional.empty(), Optional.empty());
        TransactionListFilter none = TransactionListFilter.none();

        assertThat(manual).isEqualTo(none);
    }
}
