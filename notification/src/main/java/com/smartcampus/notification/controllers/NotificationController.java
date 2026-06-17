package com.smartcampus.notification.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(service.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Notification n = service.getById(id);
        if (n == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Notification not found: " + id));
        }
        return ResponseEntity.ok(n);
    }

    @PostMapping
    public ResponseEntity<Notification> send(@RequestBody Map<String, String> body) {
        String type = body.get("type");
        String message = body.get("message");
        Notification n = service.sendNotification(type, message);
        return ResponseEntity.status(201).body(n);
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