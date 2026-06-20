package com.smartcampus.enrollment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcampus.enrollment.models.Enrollment;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // fetch everything a specific student is registered for
    List<Enrollment> findByStudentId(String studentId);
    
    // check how many students are actively in a class for capacity validation
    long countByCourseCourseCodeAndStatus(String courseCode, String status);
}