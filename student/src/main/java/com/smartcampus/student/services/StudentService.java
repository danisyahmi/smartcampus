package com.smartcampus.student.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smartcampus.student.models.Student;

@Service
public class StudentService {
    // Student List 
    private final List<Student> students = new ArrayList<>(List.of(
            new Student(1, "Alice Tan", "alice@utem.edu.my"),
            new Student(2, "Bob Lim", "bob@utem.edu.my"),
            new Student(3, "Chloe Wong", "chloe@utem.edu.my")));
    // Increment ID
    private int nextId = 4;

    // List all student
    public List<Student> findAll() {
        return students;
    }

    // Find student by id
    public Optional<Student> findById(int id) {
        return students.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    // Add new student
    public Student add(Student s) {
        s.setId(nextId++);
        students.add(s);
        return s;
    }

    // Signal if student exist
    public boolean delete(int id) {
        return students.removeIf(b -> b.getId() == id); // remove success ? return true : false
    }

}
