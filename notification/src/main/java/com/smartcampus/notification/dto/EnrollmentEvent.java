package com.smartcampus.notification.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class EnrollmentEvent implements Serializable {
    private String studentEmail;
    private String courseName;

    public EnrollmentEvent() {}

    public EnrollmentEvent(String studentEmail, String courseName) {
        this.studentEmail = studentEmail;
        this.courseName = courseName;
    }
}
