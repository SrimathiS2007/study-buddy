package com.studybuddy.controller;

import com.studybuddy.dto.Dtos.*;
import com.studybuddy.service.MembershipService;
import com.studybuddy.service.StudyGroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Validated
public class StudyGroupController {

    private final StudyGroupService groupService;
    private final MembershipService membershipService;

    public StudyGroupController(StudyGroupService groupService, MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    /** CREATE group. 201 | 400 invalid | 404 subject/creator not found | 409 duplicate group name */
    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody CreateGroupRequest r) {
        GroupResponse created = groupService.create(r);
        return ResponseEntity.created(URI.create("/api/groups/" + created.id())).body(created);
    }

    /** LIST groups with counts. Optional ?subjectId= and ?onlyOpen=true. 200 | 404 unknown subject */
    @GetMapping
    public List<GroupResponse> list(@RequestParam(required = false) @Positive Long subjectId,
                                    @RequestParam(defaultValue = "false") boolean onlyOpen) {
        return groupService.list(subjectId, onlyOpen);
    }

    /** 200 | 404 */
    @GetMapping("/{id}")
    public GroupResponse get(@PathVariable @Positive Long id) {
        return groupService.get(id);
    }

    /** UPDATE name/capacity (creator only). 200 | 400 | 403 not creator | 404 | 409 capacity < members */
    @PutMapping("/{id}")
    public GroupResponse update(@PathVariable @Positive Long id, @Valid @RequestBody UpdateGroupRequest r) {
        return groupService.update(id, r);
    }

    /** DELETE group (creator only). 204 | 403 | 404 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id,
                                       @RequestParam @NotNull @Positive Long requesterId) {
        groupService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    /** JOIN group. 201 | 400 | 404 group/student | 409 already a member OR group is full */
    @PostMapping("/{id}/members")
    public ResponseEntity<MemberResponse> join(@PathVariable @Positive Long id,
                                               @Valid @RequestBody JoinRequest r) {
        MemberResponse m = membershipService.join(id, r.studentId());
        return ResponseEntity.created(URI.create("/api/groups/" + id + "/members")).body(m);
    }

    /** LIST members. 200 | 404 */
    @GetMapping("/{id}/members")
    public List<MemberResponse> members(@PathVariable @Positive Long id) {
        return membershipService.listMembers(id);
    }

    /**
     * LEAVE (requesterId == studentId) or REMOVE a member (requesterId == creator).
     * 204 | 403 not allowed / creator can't leave | 404 group or not a member
     */
    @DeleteMapping("/{id}/members/{studentId}")
    public ResponseEntity<Void> removeMember(@PathVariable @Positive Long id,
                                             @PathVariable @Positive Long studentId,
                                             @RequestParam @NotNull @Positive Long requesterId) {
        membershipService.removeMember(id, studentId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
