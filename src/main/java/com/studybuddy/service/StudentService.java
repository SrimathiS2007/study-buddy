package com.studybuddy.service;

import com.studybuddy.dto.Dtos.StudentRequest;
import com.studybuddy.dto.Dtos.StudentResponse;
import com.studybuddy.exception.ConflictException;
import com.studybuddy.exception.ResourceNotFoundException;
import com.studybuddy.model.Student;
import com.studybuddy.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository students;

    public StudentService(StudentRepository students) {
        this.students = students;
    }

    @Transactional
    public StudentResponse register(StudentRequest r) {
        String email = r.email().trim().toLowerCase();
        if (students.existsByEmailIgnoreCase(email))
            throw new ConflictException("Email '" + email + "' is already registered");
        return toResponse(students.save(new Student(r.name().trim(), email)));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> list() {
        return students.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest r) {
        Student s = find(id);
        String email = r.email().trim().toLowerCase();
        if (students.existsByEmailIgnoreCaseAndIdNot(email, id))
            throw new ConflictException("Email '" + email + "' is already used by another student");
        s.setName(r.name().trim());
        s.setEmail(email);
        return toResponse(students.save(s));
    }

    public Student find(Long id) {
        return students.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    private StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getId(), s.getName(), s.getEmail());
    }
}
