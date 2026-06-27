package com.smartcampus.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentNotificationEvent {
    private String matricNo;
    private String type;
    private String message;
}