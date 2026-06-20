package com.smartcampus.enrollment.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentEmail;
    private String courseName;
    private String semester;
}
