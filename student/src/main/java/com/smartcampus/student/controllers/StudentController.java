package com.smartcampus.student.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.student.models.Student;
import com.smartcampus.student.services.StudentService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    StudentController(StudentService service) {
        this.service = service;
    }

    // List all students{
    @GetMapping("/")
    public List<Student> getAllStudents() {
        return service.findAll();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/matric/{studentId}")
    public ResponseEntity<Student> getByStudentId(@PathVariable String studentId) {
        return service.findByStudentId(studentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Add new student
    @PostMapping("/")
    public ResponseEntity<Student> create(@RequestBody Student student) {
        // Validate against the new JPA model fields
        if (student.getEmail() == null || student.getEmail().isBlank() ||
                student.getFirstName() == null || student.getFirstName().isBlank() ||
                student.getLastName() == null || student.getLastName().isBlank() ||
                student.getStudentId() == null || student.getStudentId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Student created = service.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update existing user data
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return service.update(id, student).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Delete student by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().<Void>build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "student",
                "status", "UP",
                "port", "8081");
    }
}
