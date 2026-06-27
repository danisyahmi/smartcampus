package com.smartcampus.student.services;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.smartcampus.student.dto.StudentEvent;
import com.smartcampus.student.models.Student;
import com.smartcampus.student.repositories.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY = "routing.student";

    public StudentService(StudentRepository studentRepository, RabbitTemplate rabbitTemplate) {
        this.studentRepository = studentRepository;
        this.rabbitTemplate = rabbitTemplate;
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
        // Prevent duplicate Matric number
        if (studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Student with Matric Number '" + student.getStudentId() + "' already exists.");
        }

        Student savedStudent = studentRepository.save(student);

        pushNotification(savedStudent.getStudentId(), "CREATE",
                "Student profile created for " + savedStudent.getFirstName() + " " + savedStudent.getLastName());
        return savedStudent;
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
            Student savedStudent = studentRepository.save(existingStudentData);

            pushNotification(savedStudent.getStudentId(), "UPDATE", "Student profile update");

            return savedStudent;
        });
    }

    // Signal if student exist
    public boolean delete(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            studentRepository.deleteById(id);

            pushNotification(student.getStudentId(), "DELETE",
                    "Student profile deleted for " + student.getFirstName());

            return true;
        } else {
            return false;
        }
    }

    // Helper method for asynchronous message choreography
    private void pushNotification(String matricNo, String type, String message) {
        try {
            StudentEvent event = new StudentEvent(matricNo, type, message);

            System.out.println("Student database commit complete! Pushing payload to RabbitMQ...");
            System.out.println("Pushing to Exchange: [" + EXCHANGE_NAME + "] with Key: [" + ROUTING_KEY + "]");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        } catch (Exception ex) {
            // Log messaging error cleanly so the actual database mutation doesn't roll back
            System.err.println("Non-blocking failure: Messaging broker down. Logged: " + ex.getMessage());
        }
    }

}
