package com.edu.edupage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "additional_courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Dance", "Music", "English Course"

    private String description;

    private String instructor; // Name of the instructor

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private String room;

    private Integer maxCapacity;

    @ManyToMany(mappedBy = "additionalCourses")
    @Builder.Default
    private Set<Student> enrolledStudents = new HashSet<>();
}
