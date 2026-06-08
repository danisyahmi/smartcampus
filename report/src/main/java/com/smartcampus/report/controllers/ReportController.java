package com.smartcampus.report.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "report",
                "status", "UP",
                "port", "8085");
    }
}
