package com.smartcampus.booking.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcampus.booking.models.Booking;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Checks if a Discussion Room is booked at a specific time
    boolean existsByResourceIdAndStartTimeAndStatus(String resourceId, LocalDateTime startTime, String status);

    // Checks if a Library Book is currently active/loaned out (ignoring time)
    boolean existsByResourceIdAndStatus(String resourceId, String status);
}
