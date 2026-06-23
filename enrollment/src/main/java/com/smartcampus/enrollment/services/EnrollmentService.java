package com.smartcampus.enrollment.services;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.enrollment.config.RabbitMQConfig;
import com.smartcampus.enrollment.dto.EnrollmentEvent;
import com.smartcampus.enrollment.models.Course;
import com.smartcampus.enrollment.models.Enrollment;
import com.smartcampus.enrollment.repositories.CourseRepository;
import com.smartcampus.enrollment.repositories.EnrollmentRepository;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final TransactionTemplate transactionTemplate;

    @Value("${external.student-service.url:http://localhost:8081}")
    private String studentServiceUrl;

    
    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            RabbitTemplate rabbitTemplate,
            PlatformTransactionManager transactionManager) {

        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = new RestTemplate();
        
        // concurrency part
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Enrollment enrol(String studentId, String courseCode, String semester) {

        // student validation
        String url = studentServiceUrl + "/api/students/matric/" + studentId;
        try {
            restTemplate.getForObject(url, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Student " + studentId + " does not exist.");
        } catch (Exception e) {
            throw new IllegalStateException("Student service unreachable.");
        }

        // only one thread can modify same course safely
        Enrollment saved = transactionTemplate.execute(status -> {

            Course course = courseRepository.findByCourseCodeForUpdate(courseCode)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Course not found: " + courseCode));

            long count = enrollmentRepository
                    .countByCourseCourseCodeAndSemesterAndStatus(courseCode, semester, "ENROLLED");

            if (count >= course.getCapacity()) {
                throw new IllegalStateException("Course is full.");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourse(course);
            enrollment.setSemester(semester);
            enrollment.setStatus("ENROLLED");

            return enrollmentRepository.save(enrollment);
        });

        try {
            EnrollmentEvent event = new EnrollmentEvent(studentId, courseCode, semester);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    event);
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