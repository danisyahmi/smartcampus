package com.smartcampus.enrollment.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import com.smartcampus.enrollment.models.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    // PESSIMISTIC_WRITE issues a SELECT ... FOR UPDATE
    // This locks the row so concurrent enrol() calls for the same course
    // are forced to wait their turn instead of racing each other.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.courseCode = :courseCode")
    Optional<Course> findByCourseCodeForUpdate(String courseCode);
}

