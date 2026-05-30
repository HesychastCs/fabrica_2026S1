package com.example.demo.application.repository;

import com.example.demo.domain.model.Report;

public interface ReportRepositoryPort {
    Report save(Report report);
}
