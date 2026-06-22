package com.smartcampus.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EnrollmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrollmentApplication.class, args);
    }
}