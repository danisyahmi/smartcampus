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

    // Dynamically injects the network URL from Docker
    @Value("${external.student-service.url:http://localhost:8081}")
    private String studentServiceUrl;

    // RabbitMQ Constants from your config
    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY = "routing.enrolment";

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            RabbitTemplate rabbitTemplate) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = new RestTemplate(); // Standard runtime instantiation
    }

    // Enrol a student into a course
    public Enrollment enrol(String studentId, String courseCode, String semester) {

        // Validate student exists by calling student profile service via Nginx
        String validationUrl = studentServiceUrl + "/api/students/matric/" + studentId;
        try {
            // Network verification call
            restTemplate.getForObject(validationUrl, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Validation Failure: Student " + studentId + " does not exist.");
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Student Service is currently unreachable. Graceful degradation triggered.");
        }

        // Fetch the target course from the internal catalog database
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course code " + courseCode + " not found."));

        // Verify class capacity constraint
        long activeEnrolledCount = enrollmentRepository.countByCourseCourseCodeAndStatus(courseCode, "ENROLLED");
        if (course.getCapacity() != null && course.getCapacity() > 0) {
            if (activeEnrolledCount >= course.getCapacity()) {
                throw new IllegalStateException("Cannot enroll: Course " + courseCode + " has reached capacity.");
            }
        }

        // Save the permanent record to enrollment_db
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourse(course);
        enrollment.setSemester(semester);
        enrollment.setStatus("ENROLLED");

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Asynchronous choreography message push
        try {
            // Creating message payload for Notification service
        	EnrollmentEvent event = new EnrollmentEvent(studentId, course.getTitle(), semester);
            
            System.out.println("Enrolment database commit complete! Pushing payload to RabbitMQ...");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        } catch (Exception ex) {
            // Log messaging error cleanly so the actual database mutation doesn't roll back
            System.err.println("Non-blocking failure: Messaging broker down. Logged: " + ex.getMessage());
        }

        return savedEnrollment;
    }

    // Get all enrollments for a specific student
    public List<Enrollment> getByStudent(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    // Get all system enrollments
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }
}