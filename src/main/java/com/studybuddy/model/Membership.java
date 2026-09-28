package com.studybuddy.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Join entity between Student and StudyGroup (Student <-> StudyGroup is many-to-many).
 * The unique constraint is the DB-level safety net for "a student cannot join the same group twice";
 * the service layer checks it first.
 */
@Entity
@Table(name = "memberships",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_student",
                columnNames = {"study_group_id", "student_id"}))
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    protected Membership() {}

    public Membership(StudyGroup studyGroup, Student student) {
        this.studyGroup = studyGroup;
        this.student = student;
    }

    public Long getId() { return id; }
    public StudyGroup getStudyGroup() { return studyGroup; }
    public Student getStudent() { return student; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}
