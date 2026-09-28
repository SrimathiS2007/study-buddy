package com.studybuddy.repository;

import com.studybuddy.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    long countByStudyGroupId(Long groupId);

    boolean existsByStudyGroupIdAndStudentId(Long groupId, Long studentId);

    Optional<Membership> findByStudyGroupIdAndStudentId(Long groupId, Long studentId);

    List<Membership> findByStudyGroupIdOrderByJoinedAtAsc(Long groupId);

    List<Membership> findByStudentId(Long studentId);
}
