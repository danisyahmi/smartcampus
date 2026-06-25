package com.smartcampus.notification.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartcampus.notification.NotificationRepository;
import com.smartcampus.notification.models.Notification;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    public Notification sendNotification(String type, String message) {
        if (type == null || type.isBlank() || message == null || message.isBlank()) {
            return null;
        }
        Notification n = new Notification(
            UUID.randomUUID().toString(),
            type,
            message,
            LocalDateTime.now().toString()
        );
        return repository.save(n);
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Notification getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public boolean markAsRead(String id) {
        Optional<Notification> optional = repository.findById(id);
        if (optional.isEmpty()) return false;
        Notification n = optional.get();
        n.setRead(true);
        repository.save(n);
        return true;
    }
}