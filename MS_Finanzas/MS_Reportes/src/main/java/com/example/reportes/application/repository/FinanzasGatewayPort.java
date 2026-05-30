package com.example.reportes.application.repository;

import java.util.List;
import java.util.UUID;

import com.example.reportes.domain.model.FinanzasTransaction;

public interface FinanzasGatewayPort {

    void ensureTitularExists(UUID titularId);

    List<FinanzasTransaction> listTransactions(UUID titularId, int mes, int anho);
}
