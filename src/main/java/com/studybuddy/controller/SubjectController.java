package com.studybuddy.controller;

import com.studybuddy.dto.Dtos.GroupResponse;
import com.studybuddy.dto.Dtos.SubjectRequest;
import com.studybuddy.dto.Dtos.SubjectResponse;
import com.studybuddy.service.StudyGroupService;
import com.studybuddy.service.SubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@Validated
public class SubjectController {

    private final SubjectService subjectService;
    private final StudyGroupService groupService;

    public SubjectController(SubjectService subjectService, StudyGroupService groupService) {
        this.subjectService = subjectService;
        this.groupService = groupService;
    }

    /** 201 Created | 400 invalid | 409 duplicate name */
    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest r) {
        SubjectResponse created = subjectService.create(r);
        return ResponseEntity.created(URI.create("/api/subjects/" + created.id())).body(created);
    }

    @GetMapping
    public List<SubjectResponse> list() {
        return subjectService.list();
    }

    /** 200 | 404 */
    @GetMapping("/{id}")
    public SubjectResponse get(@PathVariable @Positive Long id) {
        return subjectService.get(id);
    }

    /** All groups of a subject with member counts. 200 | 404 unknown subject. ?onlyOpen=true hides full groups */
    @GetMapping("/{id}/groups")
    public List<GroupResponse> groups(@PathVariable @Positive Long id,
                                      @RequestParam(defaultValue = "false") boolean onlyOpen) {
        return groupService.list(id, onlyOpen);
    }
}
