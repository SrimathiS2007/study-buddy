package com.studybuddy.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** A student. One Student has MANY Memberships (joins many groups). */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @OneToMany(mappedBy = "student")
    private List<Membership> memberships = new ArrayList<>();

    protected Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Membership> getMemberships() { return memberships; }
}
