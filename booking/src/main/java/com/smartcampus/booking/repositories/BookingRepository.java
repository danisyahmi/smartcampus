//package com.smartcampus.booking.repositories;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import com.smartcampus.booking.models.Booking;
//
//@Repository
//public interface BookingRepository extends JpaRepository<Booking, Long> {
//}

package com.smartcampus.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcampus.booking.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}