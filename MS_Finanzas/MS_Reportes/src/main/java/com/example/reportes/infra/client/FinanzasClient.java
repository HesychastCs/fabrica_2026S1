package com.example.reportes.infra.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.reportes.infra.client.dto.FinanzasTitularResponse;
import com.example.reportes.infra.client.dto.FinanzasTransactionResponse;

@FeignClient(name = "finanzas", url = "${finanzas.api.base-url}")
public interface FinanzasClient {

    @GetMapping("/api/titulares/{id}")
    FinanzasTitularResponse getTitular(@PathVariable("id") UUID id);

    @GetMapping("/api/transactions")
    List<FinanzasTransactionResponse> listTransactions(
        @RequestParam("titularId") UUID titularId,
        @RequestParam("mes") String mes
    );
}
