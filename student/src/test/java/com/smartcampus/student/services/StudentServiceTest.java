package com.smartcampus.student.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartcampus.student.models.Student;
import com.smartcampus.student.repositories.StudentRepository;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    // 1. Mock the database layer so we don't need a real MySQL connection
    @Mock
    private StudentRepository studentRepository;

    // 2. Inject the fake repository into the real service we are testing
    @InjectMocks
    private StudentService studentService;

    @Test
    public void testFindAll() {
        // Arrange
        Student s1 = new Student(); s1.setId(1L); s1.setFirstName("Alice");
        Student s2 = new Student(); s2.setId(2L); s2.setFirstName("Bob");
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        // Act
        List<Student> result = studentService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    public void testFindById_Found() {
        // Arrange
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Alice");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));

        // Act
        Optional<Student> result = studentService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testAddStudent() {
        // Arrange
        Student newStudent = new Student(); 
        newStudent.setFirstName("Charlie");

        Student savedStudent = new Student(); 
        savedStudent.setId(1L); 
        savedStudent.setFirstName("Charlie");
        
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // Act
        Student result = studentService.add(newStudent);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Charlie", result.getFirstName());
        verify(studentRepository, times(1)).save(newStudent);
    }

    @Test
    public void testUpdateStudent_Success() {
        // Arrange
        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setFirstName("OldName");
        existingStudent.setEmail("old@test.com");

        Student updatedData = new Student();
        updatedData.setFirstName("NewName");
        updatedData.setEmail("new@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        // Return whatever is passed into save() to simulate database behavior
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<Student> result = studentService.update(1L, updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("NewName", result.get().getFirstName());
        assertEquals("new@test.com", result.get().getEmail());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    public void testUpdateStudent_NotFound() {
        // Arrange
        Student updatedData = new Student();
        updatedData.setFirstName("NewName");
        
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.update(99L, updatedData);

        // Assert
        assertFalse(result.isPresent());
        // Verify save was NEVER called because the student wasn't found
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testDeleteStudent_Success() {
        // Arrange
        when(studentRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = studentService.delete(1L);

        // Assert
        assertTrue(result);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteStudent_NotFound() {
        // Arrange
        when(studentRepository.existsById(99L)).thenReturn(false);

        // Act
        boolean result = studentService.delete(99L);

        // Assert
        assertFalse(result);
        // Verify we didn't accidentally try to delete a non-existent ID
        verify(studentRepository, never()).deleteById(anyLong());
    }
}