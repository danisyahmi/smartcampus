package com.smartcampus.enrollment.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.enrollment.models.Enrollment;
import com.smartcampus.enrollment.services.EnrollmentService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService service;

    EnrollmentController(EnrollmentService s) {
        this.service = s;
    }

    @PostMapping("/")
    public ResponseEntity<?> enrol(@RequestBody Map<String, Object> body) {
        try {
            int studentId = (int) body.get("studentId");
            String courseCode = (String) body.get("courseCode");
            String semester = (String) body.get("semester");
            Enrollment e = service.enrol(studentId, courseCode, semester);
            return ResponseEntity.status(201).body(e);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> getByStudent(
            @PathVariable int studentId) {
        return service.getByStudent(studentId);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "enrollment",
                "status", "UP",
                "port", "8082");
    }
}
