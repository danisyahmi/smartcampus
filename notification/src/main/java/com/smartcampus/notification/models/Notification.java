package com.smartcampus.notification.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    private String id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String timestamp;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "matric_no")
    private String matricNo;

    public Notification() {}

    public Notification(String id, String type, String message, String timestamp, String matricNo) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.read = false;
        this.matricNo = matricNo;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public String getMatricNo() { return matricNo; }

    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { this.read = read; }
    public void setMatricNo(String matricNo) { this.matricNo = matricNo; }
}