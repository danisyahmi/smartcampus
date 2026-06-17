package com.smartcampus.notification.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smartcampus.notification.dto.EnrollmentEvent;
import com.smartcampus.notification.services.NotificationService;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    // tells Spring to watch this queue constantly
    @RabbitListener(queues = "notification.queue")
    public void receiveEnrolmentNotification(EnrollmentEvent event) {
        System.out.println("Received message from broker for student: " + event.getStudentEmail());

        // pass the data down to core business logic service to send an message
        notificationService.sendNotification(event.getStudentEmail(), "Enrolled into " + event.getCourseName());
    }
}
