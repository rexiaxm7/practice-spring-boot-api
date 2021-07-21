package com.example.demo.rest.repository;

import com.example.demo.rest.bean.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> {
}
