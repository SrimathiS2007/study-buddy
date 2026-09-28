package com.studybuddy.service;

import com.studybuddy.dto.Dtos.SubjectRequest;
import com.studybuddy.dto.Dtos.SubjectResponse;
import com.studybuddy.exception.ConflictException;
import com.studybuddy.exception.ResourceNotFoundException;
import com.studybuddy.model.Subject;
import com.studybuddy.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjects;

    public SubjectService(SubjectRepository subjects) {
        this.subjects = subjects;
    }

    @Transactional
    public SubjectResponse create(SubjectRequest r) {
        String name = r.name().trim();
        if (subjects.existsByNameIgnoreCase(name))
            throw new ConflictException("Subject '" + name + "' already exists");
        return toResponse(subjects.save(new Subject(name, r.description())));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> list() {
        return subjects.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse get(Long id) {
        return toResponse(find(id));
    }

    public Subject find(Long id) {
        return subjects.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    private SubjectResponse toResponse(Subject s) {
        return new SubjectResponse(s.getId(), s.getName(), s.getDescription());
    }
}
