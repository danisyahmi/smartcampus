package com.smartcampus.student.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smartcampus.student.models.Student;
import com.smartcampus.student.repositories.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // List all student
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    // Find student by id
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    
    public Optional<Student> findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    // Add new student
    public Student add(Student student) {
        return studentRepository.save(student);
    }

    // Update existing student data
    public Optional<Student> update(Long id, Student updatedStudentData) {
        return studentRepository.findById(id).map(existingStudentData -> {
            // update the allowed fields
            existingStudentData.setFirstName(updatedStudentData.getFirstName());
            existingStudentData.setLastName(updatedStudentData.getLastName());
            existingStudentData.setEmail(updatedStudentData.getEmail());
            existingStudentData.setProgramme(updatedStudentData.getProgramme());

            // save the changes back to MySQL
            return studentRepository.save(existingStudentData);
        });
    }

    // Signal if student exist
    public boolean delete(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
