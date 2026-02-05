package com.edu.edupage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    private String studentNumber; // Optional student ID number

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_additional_groups", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "class_group_id"))
    @Builder.Default
    private Set<ClassGroup> additionalClassGroups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_additional_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    @Builder.Default
    private Set<AdditionalCourse> additionalCourses = new HashSet<>();

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Parent> parents = new HashSet<>();
}
