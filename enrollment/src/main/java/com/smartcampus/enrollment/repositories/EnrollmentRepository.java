package com.smartcampus.enrollment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcampus.enrollment.models.Course;
import com.smartcampus.enrollment.models.Enrollment;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(String studentId);

    long countByCourseCourseCodeAndStatus(String courseCode, String status);

    void deleteByCourse(Course course);
}