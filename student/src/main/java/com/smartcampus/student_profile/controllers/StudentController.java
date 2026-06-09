package com.smartcampus.student.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.student.models.Student;

@RestController
@RequestMapping("/api/students")
public class StudentController {
 private final List<Student> students = new ArrayList<>(List.of(
        new Student(1, "Alice Tan",  "alice@utem.edu.my"),
        new Student(2, "Bob Lim",    "bob@utem.edu.my"),
        new Student(3, "Chloe Wong", "chloe@utem.edu.my")
    ));

    @GetMapping
    public List<Student> getAllStudents() {
        return students;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable int id) {
        return students.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Student not found: " + id));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "service", "student",
            "status",  "UP",
            "port",    "8081"
        );
    }
}

