package com.smartcampus.enrollment.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.smartcampus.enrollment.models.Enrollment;
import com.smartcampus.enrollment.models.Course;
import com.smartcampus.enrollment.repositories.EnrollmentRepository;
import com.smartcampus.enrollment.repositories.CourseRepository;
import com.smartcampus.enrollment.dto.EnrollmentEvent;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Value("${external.student-service.url:http://localhost:8081}")
    private String studentServiceUrl;

    private static final String EXCHANGE_NAME = "enrollment-exchange";
    private static final String ROUTING_KEY = "enrollment.success";

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            RabbitTemplate rabbitTemplate) {

        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = new RestTemplate();
    }

    public Enrollment enrol(String studentId, String courseCode, String semester) {

        // 1. Validate student
        String url = studentServiceUrl + "/api/students/matric/" + studentId;

        try {
            restTemplate.getForObject(url, Object.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Student " + studentId + " does not exist.");

        } catch (Exception e) {
            throw new IllegalStateException("Student service unreachable.");
        }

        // 2. Get course
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course not found: " + courseCode));

        // 3. Capacity check
        long count = enrollmentRepository
                .countByCourseCourseCodeAndStatus(courseCode, "ENROLLED");

        if (count >= course.getCapacity()) {
            throw new IllegalStateException("Course is full.");
        }

        // 4. Save enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourse(course);
        enrollment.setSemester(semester);
        enrollment.setStatus("ENROLLED");

        Enrollment saved = enrollmentRepository.save(enrollment);

        // 5. Send RabbitMQ event
        try {
            EnrollmentEvent event =
                    new EnrollmentEvent(studentId, courseCode, semester);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);

        } catch (Exception e) {
            System.err.println("RabbitMQ failed: " + e.getMessage());
        }

        return saved;
    }

    public List<Enrollment> getByStudent(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }
}