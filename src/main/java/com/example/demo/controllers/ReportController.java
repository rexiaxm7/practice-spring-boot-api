package com.example.demo.controllers;

import com.example.demo.bean.Report;
import com.example.demo.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Report> reportListGet() {
        return reportService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Optional<Report> reportGet(@PathVariable("id") int id) {
        return reportService.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Report reportCreate(@RequestBody Report report) {
        return reportService.create(report);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Report reportUpdate(@PathVariable("id") int id,@RequestBody Report report) {
        report.setId(id);
        return reportService.update(report);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void reportDelete(@PathVariable("id") int id) {
        reportService.delete(id);
    }
}
