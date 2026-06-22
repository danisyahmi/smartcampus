package com.smartcampus.enrollment.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal DB auto-increment primary key

    @Column(name = "course_code", unique = true, nullable = false)
    private String courseCode; // e.g., "BITM2113"

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private Integer capacity; // Max students allowed (Used for Requirement R5/R9 checks)
}