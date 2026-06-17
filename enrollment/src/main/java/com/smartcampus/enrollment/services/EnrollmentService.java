package com.smartcampus.enrollment.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.smartcampus.enrollment.config.RabbitMQConfig;
import com.smartcampus.enrollment.dto.EnrollmentEvent;
import com.smartcampus.enrollment.models.Enrollment;

@Service
public class EnrollmentService {
    private final List<Enrollment> enrollments = new ArrayList<>();
    private int nextId = 100;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // URL of the Student Service
    private static final String STUDENT_URL = "http://localhost:8081/api/students/";

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

        try {
            // faking a dummy email
            String studentEmail = "student" + studentId + "@smartcampus.edu";

            EnrollmentEvent event = new EnrollmentEvent(studentEmail, courseCode);

            System.out.println("Enrolment successful! Pushing message to broker exchange...");
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    event);
        } catch (Exception ex) {
            // Log the error but don't crash the user interface
            System.err.println("Failed to queue notification event: " + ex.getMessage());
        }

        return e;
    }

    public List<Enrollment> getByStudent(int studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId() == studentId).toList();
    }

}
