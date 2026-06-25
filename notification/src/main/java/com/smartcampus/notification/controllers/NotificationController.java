package com.smartcampus.notification.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.notification.models.Notification;
import com.smartcampus.notification.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(service.getAllNotifications());
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "Notification service is temporarily unavailable",
                "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Notification n = service.getById(id);
            if (n == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Notification not found: " + id));
            }
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "Notification service is temporarily unavailable",
                "details", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        try {
            String type = body.get("type");
            String message = body.get("message");
            Notification n = service.sendNotification(type, message);
            if (n == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Fields 'type' and 'message' are required"));
            }
            return ResponseEntity.status(201).body(n);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "Notification service is temporarily unavailable",
                "details", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        try {
            boolean updated = service.markAsRead(id);
            if (!updated) {
                return ResponseEntity.status(404).body(Map.of("error", "Notification not found: " + id));
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "Notification service is temporarily unavailable",
                "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "service", "notification",
            "status", "UP",
            "port", "8083"
        ));
    }
}