package com.smartcampus.booking.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.booking.models.Booking;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
        // USER_SERVICE_URL is injected from environment variable
        private final String studentServiceUrl = System.getenv().getOrDefault(
                        "USER_SERVICE_URL",
                        "http://localhost:8081");

        private final List<Booking> bookings = new ArrayList<>(List.of(
                        new Booking(101, 1, "Java Programming Book", 89.90),
                        new Booking(102, 2, "Wireless Mouse", 45.00),
                        new Booking(103, 1, "USB-C Hub", 120.00)));

        @GetMapping
        public List<Booking> getAll() {
                return bookings;
        }

        @GetMapping("/student/{studentId}")
        public Map<String, Object> getbookingsForstudent(
                        @PathVariable int studentId) {
                // Call student Service to verify student exists
                RestTemplate rt = new RestTemplate();
                Object student = rt.getForObject(
                                studentServiceUrl + "/api/students/" + studentId,
                                Object.class);

                List<Booking> studentBookings = bookings.stream()
                                .filter(o -> o.getStudentId() == studentId)
                                .toList();

                return Map.of(
                                "student", student,
                                "bookings", studentBookings);
        }

        @GetMapping("/health")
        public Map<String, String> health() {
                return Map.of(
                                "service", "booking-service",
                                "status", "UP",
                                "port", "8084");
        }
}
