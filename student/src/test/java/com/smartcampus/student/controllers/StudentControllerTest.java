package com.smartcampus.student.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.student.models.Student;
import com.smartcampus.student.services.StudentService;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Jackson's ObjectMapper is automatically provided by Spring to convert Java objects to JSON strings
    @Autowired
    private ObjectMapper objectMapper;

    // Creates a fake service layer so we bypass the database entirely
    @MockBean
    private StudentService service;

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/students/health"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    public void testGetAllStudents() throws Exception {
        Student s1 = new Student();
        s1.setFirstName("Alice");
        
        when(service.findAll()).thenReturn(List.of(s1));

        // Note: Using "/" because your controller explicitly maps @GetMapping("/")
        mockMvc.perform(get("/api/students/"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    public void testGetStudentById_Success() throws Exception {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setStudentId("B100");
        
        when(service.findById(1L)).thenReturn(Optional.of(s1));

        mockMvc.perform(get("/api/students/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.studentId").value("B100"));
    }

    @Test
    public void testGetStudentById_NotFound() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/99"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateStudent_Success() throws Exception {
        Student newStudent = new Student();
        newStudent.setFirstName("Bob");
        newStudent.setLastName("Builder");
        newStudent.setEmail("bob@test.com");
        newStudent.setStudentId("B200");

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setFirstName("Bob");

        when(service.add(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/api/students/")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(newStudent)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.firstName").value("Bob"));
    }

    @Test
    public void testCreateStudent_BadRequest() throws Exception {
        Student badStudent = new Student();
        // Missing required fields like email and studentId to trigger your validation logic
        badStudent.setFirstName("Bob");

        mockMvc.perform(post("/api/students/")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(badStudent)))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateStudent_Success() throws Exception {
        Student updatedData = new Student();
        updatedData.setFirstName("Charlie");

        when(service.update(eq(1L), any(Student.class))).thenReturn(Optional.of(updatedData));

        mockMvc.perform(put("/api/students/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedData)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Charlie"));
    }

    @Test
    public void testUpdateStudent_NotFound() throws Exception {
        Student updatedData = new Student();
        updatedData.setFirstName("Charlie");

        when(service.update(eq(99L), any(Student.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/students/99")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedData)))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteStudent_Success() throws Exception {
        when(service.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/students/1"))
               .andExpect(status().isNoContent()); // Expecting 204 No Content
    }

    @Test
    public void testDeleteStudent_NotFound() throws Exception {
        when(service.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/students/99"))
               .andExpect(status().isNotFound()); // Expecting 404 Not Found
    }
}