package com.example.demo.infra.persistence.repository;

import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.ReportEntityMapper;
import com.example.demo.infra.persistence.entity.ReportEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaReportRepositoryAdapter")
class JpaReportRepositoryAdapterTest {

    @Mock private JpaReportRepository jpaReportRepository;
    @Mock private ReportEntityMapper reportEntityMapper;

    @InjectMocks private JpaReportRepositoryAdapter adapter;

    private Report report;
    private ReportEntity entity;
    private TitularEntity titularEntity;

    @BeforeEach
    void setUp() {
        UUID reportId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();

        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");

        report = new Report(reportId, 5, 2025,
                BigDecimal.valueOf(3000000), BigDecimal.valueOf(1500000),
                BigDecimal.valueOf(200000), BigDecimal.valueOf(1300000),
                Instant.now(), titular);

        titularEntity = new TitularEntity();
        titularEntity.setTitularId(titularId);

        entity = new ReportEntity();
        entity.setReportId(reportId);
        entity.setMes(5);
        entity.setAnho(2025);
        entity.setIngresosAcumulados(BigDecimal.valueOf(3000000));
        entity.setGastosAcumulados(BigDecimal.valueOf(1500000));
        entity.setAportesMetaAcumulados(BigDecimal.valueOf(200000));
        entity.setBalanceNeto(BigDecimal.valueOf(1300000));
        entity.setFechaGenerado(report.fechaGenerado());
        entity.setTitular(titularEntity);
    }

    @Test
    @DisplayName("save - persiste reporte y retorna dominio mapeado")
    void save_persistsReportAndReturnsDomain() {
        when(reportEntityMapper.toEntity(report)).thenReturn(entity);
        when(jpaReportRepository.save(entity)).thenReturn(entity);
        when(reportEntityMapper.toDomain(entity, titularEntity)).thenReturn(report);

        Report result = adapter.save(report);

        assertThat(result).isEqualTo(report);
        verify(jpaReportRepository).save(entity);
        verify(reportEntityMapper).toDomain(entity, titularEntity);
    }
}
