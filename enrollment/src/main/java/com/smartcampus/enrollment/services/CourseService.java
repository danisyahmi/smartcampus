package com.smartcampus.enrollment.services;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.smartcampus.enrollment.dto.CourseDTO;
import com.smartcampus.enrollment.dto.EnrollmentEvent;
import com.smartcampus.enrollment.models.Course;
import com.smartcampus.enrollment.repositories.CourseRepository;
import com.smartcampus.enrollment.repositories.EnrollmentRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY   = "routing.enrollment";

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.rabbitTemplate   = rabbitTemplate;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course createCourse(CourseDTO dto) {
        if (courseRepository.findByCourseCode(dto.getCourseCode()).isPresent()) {
            throw new IllegalArgumentException("Course with this code already exists.");
        }

        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setTitle(dto.getTitle());
        course.setCredits(dto.getCredits());
        course.setCapacity(dto.getCapacity());

        Course saved = courseRepository.save(course);

        pushNotification("SYSTEM", "ENROLLMENT",
            "New course registered: " + saved.getCourseCode() + " - " + saved.getTitle());

        return saved;
    }

    @Transactional
    public void deleteCourse(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
            .orElseThrow(() -> new IllegalArgumentException("Course not found."));

        enrollmentRepository.deleteByCourse(course);

        courseRepository.delete(course);

        pushNotification("SYSTEM", "ENROLLMENT",
            "Course removed: " + course.getCourseCode() + " - " + course.getTitle());
    }

    private void pushNotification(String matricNo, String type, String message) {
        try {
            EnrollmentEvent event = new EnrollmentEvent(matricNo, type, message);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        } catch (Exception ex) {
            System.err.println("Non-blocking failure: Messaging broker down. Logged: " + ex.getMessage());
        }
    }
}