package com.smartcampus.student.models;

import lombok.Data;

@Data
public class Student {
    private int id;
    private String name;
    private String email;

    // Default Constructor
    public Student(int id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }

}
