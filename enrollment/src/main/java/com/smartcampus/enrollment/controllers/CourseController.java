package com.smartcampus.enrollment.controllers;

import com.smartcampus.enrollment.services.CourseService;
import com.smartcampus.enrollment.dto.CourseDTO;
import com.smartcampus.enrollment.models.Course;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000") // Ensure frontend can communicate with the backend
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping("/")
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            return ResponseEntity.ok(courseService.createCourse(courseDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{courseCode}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseCode) {
        try {
            courseService.deleteCourse(courseCode);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}