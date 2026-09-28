package com.studybuddy.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** A subject (Maths, DBMS...). One Subject has MANY StudyGroups. */
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "subject")
    private List<StudyGroup> studyGroups = new ArrayList<>();

    protected Subject() {}

    public Subject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<StudyGroup> getStudyGroups() { return studyGroups; }
}
