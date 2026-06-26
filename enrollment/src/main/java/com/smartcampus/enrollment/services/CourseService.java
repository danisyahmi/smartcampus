package com.smartcampus.enrollment.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcampus.enrollment.dto.CourseDTO;
import com.smartcampus.enrollment.models.Course;
import com.smartcampus.enrollment.repositories.CourseRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(String courseCode) {
        if (courseRepository.findByCourseCode(courseCode).isEmpty()) {
            throw new IllegalArgumentException("Course not found.");
        }
        courseRepository.deleteByCourseCode(courseCode);
    }
}