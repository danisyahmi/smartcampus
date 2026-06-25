package com.smartcampus.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcampus.booking.models.Booking;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	// Check if a room is already actively booked for a given time/slot
	boolean existsByResourceIdAndStartTimeAndStatus(
            String resourceId, LocalDateTime startTime, String status);
	
	// Check if a book is already on active 
	boolean existsByResourceIdAndStatus(String resourceId, String status);
}
