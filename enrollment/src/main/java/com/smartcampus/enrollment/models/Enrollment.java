package com.smartcampus.enrollment.models;

import lombok.Data;

@Data
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private String courseCode;
    private String semester;

    public Enrollment() {}
}
