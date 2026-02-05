package com.edu.edupage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber; // e.g., "101", "205"

    private String name; // e.g., "Biology Lab", "Computer Room"

    @Column(nullable = false)
    private Integer floor; // 1, 2, etc.

    @Builder.Default
    private Integer capacity = 30;
}
