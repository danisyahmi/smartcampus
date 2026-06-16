package com.smartcampus.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/students/health"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetStudentById_Success() throws Exception {
        // Assumes pre-seeded ID 1 exists in development database
        mockMvc.perform(get("/api/students/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetStudentById_NotFoundThrows500() throws Exception {
        // Matches your script profile behavior where a missing user throws an unhandled error
        mockMvc.perform(get("/api/students/999"))
               .andExpect(status().isInternalServerError());
    }
}