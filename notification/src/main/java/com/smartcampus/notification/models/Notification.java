package com.smartcampus.notification.models;

public class Notification {
    private String id;
    private String type;
    private String message;
    private String timestamp;

    public Notification(String id, String type, String message, String timestamp) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}