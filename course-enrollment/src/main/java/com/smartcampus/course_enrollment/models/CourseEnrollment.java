package com.smartcampus.course_enrollment.models;

import lombok.Data;

@Data
public class CourseEnrollment {
    private int enrolmentId;
    private int studentId;
    private String courseCode;
    private String semester;

    public CourseEnrollment() {}
}
