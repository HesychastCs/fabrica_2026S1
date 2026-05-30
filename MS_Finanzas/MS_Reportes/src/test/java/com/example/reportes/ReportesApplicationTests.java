package com.example.reportes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.reportes.infra.client.FinanzasClient;

@SpringBootTest
@ActiveProfiles("test")
class ReportesApplicationTests {

    @MockBean
    private FinanzasClient finanzasClient;

    @Test
    void contextLoads() {
    }
}
