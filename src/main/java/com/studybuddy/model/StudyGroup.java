package com.studybuddy.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A study group.
 *  - MANY groups belong to ONE Subject
 *  - MANY groups are created by ONE Student (creator)
 *  - ONE group has MANY Memberships
 */
@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    /** Max number of members (creator counts as a member). */
    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Student creator;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    protected StudyGroup() {}

    public StudyGroup(String name, int capacity, Subject subject, Student creator) {
        this.name = name;
        this.capacity = capacity;
        this.subject = subject;
        this.creator = creator;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Subject getSubject() { return subject; }
    public Student getCreator() { return creator; }
    public List<Membership> getMemberships() { return memberships; }
}
