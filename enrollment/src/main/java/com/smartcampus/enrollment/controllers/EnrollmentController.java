package com.smartcampus.enrollment.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartcampus.enrollment.models.Enrollment;
import com.smartcampus.enrollment.services.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/")
    public List<Enrollment> getAll() {
        return enrollmentService.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<?> createEnrollment(@RequestBody Map<String, String> requestBody) {

        String studentId = requestBody.get("studentId");
        String courseCode = requestBody.get("courseCode");
        String semester = requestBody.get("semester");

        if (studentId == null || courseCode == null || semester == null ||
                studentId.isBlank() || courseCode.isBlank() || semester.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required payload attributes."));
        }

        try {
            Enrollment result = enrollmentService.enrol(studentId, courseCode, semester);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "enrollment",
                "status", "UP",
                "port", "8082"
        );
    }
}