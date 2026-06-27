package com.smartcampus.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentNotificationEvent {
    private String studentEmail;
    private String courseName;
    private String matricNo; 
}