package com.smartcampus.booking.controllers;

import com.smartcampus.booking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/room")
    public ResponseEntity<?> createRoomBooking(@RequestBody Map<String, String> requestPayload) {
        String roomId = requestPayload.get("roomId");
        String studentId = requestPayload.get("studentId");
        String bookingDate = requestPayload.get("bookingDate");

        // Validate incoming REST properties
        if (roomId == null || studentId == null || bookingDate == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required payload keys."));
        }

        // Bridge to the legacy SOAP backend system
        Map<String, String> soapResult = bookingService.reserveLegacyRoom(roomId, studentId, bookingDate);

        if ("ERROR".equals(soapResult.get("status"))) {
            return ResponseEntity.status(502).body(soapResult); // Bad Gateway if SOAP fails
        }

        return ResponseEntity.ok(soapResult); // Returns clean JSON response to client
    }
    
    // Gateway health endpoint 
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Healthy");
    }
}