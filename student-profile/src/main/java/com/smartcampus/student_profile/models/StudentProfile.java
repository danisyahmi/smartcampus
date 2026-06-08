package com.smartcampus.student_profile.models;

import lombok.Data;

@Data
public class StudentProfile {
    private int id;
    private String name;
    private String email;

    // Default Constructor
    public StudentProfile(int id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }

}
