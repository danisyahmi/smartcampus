package com.smartcampus.notification.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.smartcampus.notification.models.Notification;

@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();

    public Notification sendNotification(String type, String message) {
        Notification n = new Notification(
            UUID.randomUUID().toString(),
            type,
            message,
            LocalDateTime.now().toString()
        );
        notifications.add(n);
        return n;
    }

    public List<Notification> getAllNotifications() {
        return notifications;
    }

    public Notification getById(String id) {
        return notifications.stream()
            .filter(n -> n.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}