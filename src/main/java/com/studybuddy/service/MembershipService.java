package com.studybuddy.service;

import com.studybuddy.dto.Dtos.GroupResponse;
import com.studybuddy.dto.Dtos.MemberResponse;
import com.studybuddy.exception.AlreadyMemberException;
import com.studybuddy.exception.ForbiddenOperationException;
import com.studybuddy.exception.GroupFullException;
import com.studybuddy.exception.ResourceNotFoundException;
import com.studybuddy.model.Membership;
import com.studybuddy.model.Student;
import com.studybuddy.model.StudyGroup;
import com.studybuddy.repository.MembershipRepository;
import com.studybuddy.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MembershipService {

    private final MembershipRepository memberships;
    private final StudyGroupRepository groups;
    private final StudyGroupService groupService;
    private final StudentService studentService;

    public MembershipService(MembershipRepository memberships, StudyGroupRepository groups,
                             StudyGroupService groupService, StudentService studentService) {
        this.memberships = memberships;
        this.groups = groups;
        this.groupService = groupService;
        this.studentService = studentService;
    }

    /**
     * JOIN. Business rules enforced HERE in the service layer (DB constraint is only the backup):
     *  1) a student cannot join the same group twice
     *  2) a group cannot accept members beyond its capacity
     * The group row is locked so concurrent joins can't overshoot the capacity.
     */
    @Transactional
    public MemberResponse join(Long groupId, Long studentId) {
        StudyGroup group = groups.findByIdForUpdate(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Study group", groupId));
        Student student = studentService.find(studentId);

        if (memberships.existsByStudyGroupIdAndStudentId(groupId, studentId))
            throw new AlreadyMemberException(studentId, groupId);

        if (memberships.countByStudyGroupId(groupId) >= group.getCapacity())
            throw new GroupFullException(groupId, group.getCapacity());

        Membership m = memberships.save(new Membership(group, student));
        return toMember(m, group);
    }

    /**
     * LEAVE (requesterId == studentId) or REMOVE (creator removes someone else).
     * The creator cannot leave / be removed; they must delete the group instead.
     */
    @Transactional
    public void removeMember(Long groupId, Long studentId, Long requesterId) {
        StudyGroup group = groupService.find(groupId);
        Long creatorId = group.getCreator().getId();

        if (studentId.equals(creatorId))
            throw new ForbiddenOperationException(
                    "The creator cannot leave or be removed; delete the group instead");

        boolean selfLeave = requesterId.equals(studentId);
        if (!selfLeave && !requesterId.equals(creatorId))
            throw new ForbiddenOperationException("Only the group creator can remove other members");

        Membership m = memberships.findByStudyGroupIdAndStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student " + studentId + " is not a member of group " + groupId));
        memberships.delete(m);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(Long groupId) {
        StudyGroup group = groupService.find(groupId);
        return memberships.findByStudyGroupIdOrderByJoinedAtAsc(groupId).stream()
                .map(m -> toMember(m, group))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> groupsOfStudent(Long studentId) {
        studentService.find(studentId); // 404 if unknown student
        return memberships.findByStudentId(studentId).stream()
                .map(m -> groupService.toResponse(m.getStudyGroup()))
                .toList();
    }

    private MemberResponse toMember(Membership m, StudyGroup group) {
        Student s = m.getStudent();
        return new MemberResponse(s.getId(), s.getName(), s.getEmail(), m.getJoinedAt(),
                s.getId().equals(group.getCreator().getId()));
    }
}
