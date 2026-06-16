package com.smartcampus.enrollment.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.enrollment.models.Enrollment;

@Service
public class EnrollmentService {
    private final List<Enrollment> enrollments = new ArrayList<>();
    private int nextId = 100;

    // URL of the Student Service
    private static final String STUDENT_URL = "http://0.0.0.0:8081/api/students/";

    public Enrollment enrol(int studentId, String courseCode, String semester) {
        
        // Validate student exists by calling Student Service
        RestTemplate rt = new RestTemplate();
        try {
            rt.getForObject(STUDENT_URL + studentId, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException(
                    "Student " + studentId + " not found");
        }
        Enrollment e = new Enrollment();
        e.setEnrollmentId(nextId++);
        e.setStudentId(studentId);
        e.setCourseCode(courseCode);
        e.setSemester(semester);
        enrollments.add(e);
        return e;
    }

    public List<Enrollment> getByStudent(int studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId() == studentId).toList();
    }

}
