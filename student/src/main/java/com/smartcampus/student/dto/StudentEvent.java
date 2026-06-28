package com.smartcampus.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEvent {
    private String matricNo;
    private String type;
    private String message;
}