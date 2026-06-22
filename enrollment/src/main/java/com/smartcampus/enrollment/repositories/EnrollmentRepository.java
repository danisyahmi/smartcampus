package com.smartcampus.enrollment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.enrollment.models.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(String studentId);

    long countByCourseCourseCodeAndStatus(
            String courseCode, String semester, String status);
}

