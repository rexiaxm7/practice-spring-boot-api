package com.example.demo.service;

import com.example.demo.bean.Report;
import com.example.demo.repository.ReportRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Optional<Report> findOne(Integer id) {
        return reportRepository.findById(id);
    }

    public Report create(Report report) { return reportRepository.save(report); }

    public Report update(Report report) {
        return reportRepository.save(report);
    }

    public void delete(Integer id) {
        reportRepository.deleteById(id);
    }
}
