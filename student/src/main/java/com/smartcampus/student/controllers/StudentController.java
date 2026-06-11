package com.smartcampus.student.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.student.models.Student;
import com.smartcampus.student.services.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    StudentController(StudentService s) {
        this.service = s;
    }

    // List all students{
    @GetMapping("/")
    public List<Student> getAllStudents() {
        return service.findAll();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable int id) {
        return service.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity
        .notFound().build());
    }

    // Add new student
    @PostMapping("/")
    public ResponseEntity<Student> create(
        @RequestBody Student student) {
        if (student.getEmail() == null || student.getName() == null || student.getEmail().isBlank() || student.getName().isBlank())
            return ResponseEntity.badRequest().build();
        Student created = service.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.delete(id) 
        ? ResponseEntity.noContent().<Void>build() 
        : ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "student",
                "status", "UP",
                "port", "8081");
    }
}
