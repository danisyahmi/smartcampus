package com.smartcampus.notification.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.smartcampus.notification.dto.StudentNotificationEvent;
import com.smartcampus.notification.services.NotificationService;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void receiveGenericNotification(StudentNotificationEvent event) {
        System.out.println("Processing notification pattern: " + event.getType() + " for student: " + event.getMatricNo());

        // matches table schema mapping
        notificationService.sendNotification(
            event.getType(), 
            event.getMessage(), 
            event.getMatricNo()
        );
    }
}