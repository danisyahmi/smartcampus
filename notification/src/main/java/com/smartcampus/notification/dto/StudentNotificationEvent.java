package com.smartcampus.notification.dto;

public class StudentNotificationEvent {
    private String type;
    private String message;
    private String matricNo;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMatricNo() { return matricNo; }
    public void setMatricNo(String matricNo) { this.matricNo = matricNo; }
}