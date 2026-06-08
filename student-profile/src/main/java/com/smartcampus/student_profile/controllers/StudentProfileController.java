package com.smartcampus.student_profile.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.student_profile.models.StudentProfile;

@RestController
@RequestMapping("/api/users")
public class StudentProfileController {
 private final List<StudentProfile> users = new ArrayList<>(List.of(
        new StudentProfile(1, "Alice Tan",  "alice@utem.edu.my"),
        new StudentProfile(2, "Bob Lim",    "bob@utem.edu.my"),
        new StudentProfile(3, "Chloe Wong", "chloe@utem.edu.my")
    ));

    @GetMapping
    public List<StudentProfile> getAllStudentProfiles() {
        return users;
    }

    @GetMapping("/{id}")
    public StudentProfile getStudentProfile(@PathVariable int id) {
        return users.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("User not found: " + id));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "service", "student-profile",
            "status",  "UP",
            "port",    "8081"
        );
    }
}

