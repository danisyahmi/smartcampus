package com.smartcampus.enrollment.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private String courseCode;
    private String title;
    private Integer credits;
    private Integer capacity;
}
