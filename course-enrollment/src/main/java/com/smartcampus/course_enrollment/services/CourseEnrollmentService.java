package com.smartcampus.course_enrollment.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.course_enrollment.models.CourseEnrollment;

@Service
public class CourseEnrollmentService {
    private final List<CourseEnrollment> enrolments = new ArrayList<>();
    private int nextId = 100;

    // URL of the Student Service
    private static final String STUDENT_URL = "http://localhost:8081/api/students/";

    public CourseEnrollment enrol(int studentId, String courseCode, String semester) {
        
        // Validate student exists by calling Student Service
        RestTemplate rt = new RestTemplate();
        try {
            rt.getForObject(STUDENT_URL + studentId, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException(
                    "Student " + studentId + " not found");
        }
        CourseEnrollment e = new CourseEnrollment();
        e.setEnrolmentId(nextId++);
        e.setStudentId(studentId);
        e.setCourseCode(courseCode);
        e.setSemester(semester);
        enrolments.add(e);
        return e;
    }

    public List<CourseEnrollment> getByStudent(int studentId) {
        return enrolments.stream()
                .filter(e -> e.getStudentId() == studentId).toList();
    }

}
