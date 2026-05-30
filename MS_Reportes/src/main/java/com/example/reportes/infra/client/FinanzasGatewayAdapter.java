package com.example.reportes.infra.client;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.reportes.application.repository.FinanzasGatewayPort;
import com.example.reportes.domain.exception.ResourceNotFoundException;
import com.example.reportes.domain.model.FinanzasTransaction;
import com.example.reportes.domain.model.TransactionType;
import com.example.reportes.infra.client.dto.FinanzasTransactionResponse;

import feign.FeignException;

@Component
public class FinanzasGatewayAdapter implements FinanzasGatewayPort {

    private final FinanzasClient finanzasClient;

    public FinanzasGatewayAdapter(FinanzasClient finanzasClient) {
        this.finanzasClient = finanzasClient;
    }

    @Override
    public void ensureTitularExists(UUID titularId) {
        try {
            finanzasClient.getTitular(titularId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("El titular no fue identificado en Finanzas");
        } catch (FeignException e) {
            throw new IllegalStateException("No se pudo consultar Finanzas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FinanzasTransaction> listTransactions(UUID titularId, int mes, int anho) {
        String mesParam = YearMonth.of(anho, mes).toString();
        try {
            return finanzasClient.listTransactions(titularId, mesParam).stream()
                .map(this::toDomain)
                .toList();
        } catch (FeignException e) {
            throw new IllegalStateException("No se pudieron obtener transacciones de Finanzas", e);
        }
    }

    private FinanzasTransaction toDomain(FinanzasTransactionResponse response) {
        return new FinanzasTransaction(
            response.transactionId(),
            response.nombre(),
            response.monto(),
            response.descripcion(),
            TransactionType.valueOf(response.tipo()),
            response.fecha(),
            response.nombreCategoria()
        );
    }
}
