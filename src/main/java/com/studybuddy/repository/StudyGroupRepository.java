package com.studybuddy.repository;

import com.studybuddy.model.StudyGroup;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    List<StudyGroup> findBySubjectId(Long subjectId);

    boolean existsBySubjectIdAndNameIgnoreCase(Long subjectId, String name);

    /** Locks the group row so two students can't take the last seat at the same moment. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from StudyGroup g where g.id = :id")
    Optional<StudyGroup> findByIdForUpdate(@Param("id") Long id);
}
