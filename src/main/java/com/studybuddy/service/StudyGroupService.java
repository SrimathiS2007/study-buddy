package com.studybuddy.service;

import com.studybuddy.dto.Dtos.CreateGroupRequest;
import com.studybuddy.dto.Dtos.GroupResponse;
import com.studybuddy.dto.Dtos.UpdateGroupRequest;
import com.studybuddy.exception.ConflictException;
import com.studybuddy.exception.ForbiddenOperationException;
import com.studybuddy.exception.ResourceNotFoundException;
import com.studybuddy.model.Membership;
import com.studybuddy.model.Student;
import com.studybuddy.model.StudyGroup;
import com.studybuddy.model.Subject;
import com.studybuddy.repository.MembershipRepository;
import com.studybuddy.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyGroupService {

    private final StudyGroupRepository groups;
    private final MembershipRepository memberships;
    private final SubjectService subjectService;
    private final StudentService studentService;

    public StudyGroupService(StudyGroupRepository groups, MembershipRepository memberships,
                             SubjectService subjectService, StudentService studentService) {
        this.groups = groups;
        this.memberships = memberships;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    /** Create a group; the creator automatically becomes its first member. */
    @Transactional
    public GroupResponse create(CreateGroupRequest r) {
        Subject subject = subjectService.find(r.subjectId());
        Student creator = studentService.find(r.creatorId());
        String name = r.name().trim();

        if (groups.existsBySubjectIdAndNameIgnoreCase(subject.getId(), name))
            throw new ConflictException("A group named '" + name + "' already exists for subject " + subject.getName());

        StudyGroup group = groups.save(new StudyGroup(name, r.capacity(), subject, creator));
        memberships.save(new Membership(group, creator));
        return toResponse(group);
    }

    /** List groups (optionally only one subject, optionally only groups with free seats) with member counts. */
    @Transactional(readOnly = true)
    public List<GroupResponse> list(Long subjectId, boolean onlyOpen) {
        List<StudyGroup> found;
        if (subjectId == null) {
            found = groups.findAll();
        } else {
            subjectService.find(subjectId); // 404 if the subject doesn't exist
            found = groups.findBySubjectId(subjectId);
        }
        return found.stream()
                .map(this::toResponse)
                .filter(g -> !onlyOpen || !g.full())
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse get(Long id) {
        return toResponse(find(id));
    }

    /** Creator can rename / change capacity. Capacity can't go below the current member count. */
    @Transactional
    public GroupResponse update(Long id, UpdateGroupRequest r) {
        StudyGroup g = groups.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study group", id));
        requireCreator(g, r.requesterId());

        long current = memberships.countByStudyGroupId(id);
        if (r.capacity() < current)
            throw new ConflictException("Cannot set capacity to " + r.capacity()
                    + ": group already has " + current + " members");

        String name = r.name().trim();
        if (!name.equalsIgnoreCase(g.getName())
                && groups.existsBySubjectIdAndNameIgnoreCase(g.getSubject().getId(), name))
            throw new ConflictException("A group named '" + name + "' already exists for this subject");

        g.setName(name);
        g.setCapacity(r.capacity());
        return toResponse(groups.save(g));
    }

    /** Only the creator can delete; memberships are removed with it (cascade). */
    @Transactional
    public void delete(Long id, Long requesterId) {
        StudyGroup g = find(id);
        requireCreator(g, requesterId);
        groups.delete(g);
    }

    // ---------- helpers (also used by MembershipService) ----------
    public StudyGroup find(Long id) {
        return groups.findById(id).orElseThrow(() -> new ResourceNotFoundException("Study group", id));
    }

    public void requireCreator(StudyGroup g, Long requesterId) {
        if (!g.getCreator().getId().equals(requesterId))
            throw new ForbiddenOperationException("Only the group creator can do this");
    }

    public GroupResponse toResponse(StudyGroup g) {
        long count = memberships.countByStudyGroupId(g.getId());
        int seatsLeft = (int) Math.max(0, g.getCapacity() - count);
        return new GroupResponse(g.getId(), g.getName(),
                g.getSubject().getId(), g.getSubject().getName(),
                g.getCreator().getId(), g.getCreator().getName(),
                g.getCapacity(), count, seatsLeft, count >= g.getCapacity(),
                g.getCreatedAt());
    }
}
