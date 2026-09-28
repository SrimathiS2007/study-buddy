package com.studybuddy.controller;

import com.studybuddy.dto.Dtos.GroupResponse;
import com.studybuddy.dto.Dtos.StudentRequest;
import com.studybuddy.dto.Dtos.StudentResponse;
import com.studybuddy.service.MembershipService;
import com.studybuddy.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    private final StudentService studentService;
    private final MembershipService membershipService;

    public StudentController(StudentService studentService, MembershipService membershipService) {
        this.studentService = studentService;
        this.membershipService = membershipService;
    }

    /** 201 Created | 400 invalid | 409 email already registered */
    @PostMapping
    public ResponseEntity<StudentResponse> register(@Valid @RequestBody StudentRequest r) {
        StudentResponse created = studentService.register(r);
        return ResponseEntity.created(URI.create("/api/students/" + created.id())).body(created);
    }

    @GetMapping
    public List<StudentResponse> list() {
        return studentService.list();
    }

    /** 200 | 404 */
    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable @Positive Long id) {
        return studentService.get(id);
    }

    /** 200 | 400 | 404 | 409 email taken */
    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable @Positive Long id, @Valid @RequestBody StudentRequest r) {
        return studentService.update(id, r);
    }

    /** Groups this student has joined. 200 | 404 */
    @GetMapping("/{id}/groups")
    public List<GroupResponse> myGroups(@PathVariable @Positive Long id) {
        return membershipService.groupsOfStudent(id);
    }
}
