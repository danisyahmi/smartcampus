package com.smartcampus.booking.controllers;

import com.smartcampus.booking.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    };

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserveRoom(@RequestBody Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String studentId = payload.get("studentId");
        String bookingDate = payload.get("bookingDate");

        if (roomId == null || studentId == null || bookingDate == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Missing payload keys."));
        }

        Map<String, String> executionOutput = bookingService.reserveLegacyRoom(roomId, studentId, bookingDate);

        // If a SOAP Fault was captured, return 422 Unprocessable or 500 error
        if ("FAULT".equalsIgnoreCase(executionOutput.get("status"))) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(executionOutput);
        }

        return ResponseEntity.ok(executionOutput);
    }

    @PostMapping("/loan")
    public ResponseEntity<Map<String, String>> loanBook(@RequestBody Map<String, String> payload) {
        String bookId = payload.get("bookId");
        String studentId = payload.get("studentId");

        if (bookId == null || studentId == null || bookId.isBlank() || studentId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Missing payload keys."));
        }

        Map<String, String> executionOutput = bookingService.loanLegacyBook(bookId, studentId);

        if ("FAULT".equalsIgnoreCase(executionOutput.get("status"))) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(executionOutput);
        }

        return ResponseEntity.ok(executionOutput);
    }

    // Gateway health endpoint
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "booking",
                "status", "UP",
                "port", "8084");
    }
}