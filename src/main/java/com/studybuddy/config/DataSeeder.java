package com.studybuddy.config;

import com.studybuddy.model.Subject;
import com.studybuddy.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Inserts a few subjects on first run so Swagger testing is easy. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SubjectRepository subjects;

    public DataSeeder(SubjectRepository subjects) {
        this.subjects = subjects;
    }

    @Override
    public void run(String... args) {
        if (subjects.count() == 0) {
            subjects.save(new Subject("Mathematics", "Calculus, algebra, statistics"));
            subjects.save(new Subject("Physics", "Mechanics, optics, modern physics"));
            subjects.save(new Subject("DBMS", "SQL, normalization, transactions"));
            subjects.save(new Subject("Java", "OOP, collections, Spring"));
        }
    }
}
