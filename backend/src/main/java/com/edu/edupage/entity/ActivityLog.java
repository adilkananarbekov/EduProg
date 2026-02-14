package com.edu.edupage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "USER_REGISTERED", "ANNOUNCEMENT_POSTED"

    @Column(nullable = false)
    private String description; // e.g., "New student John Doe registered"

    @Column(name = "user_id")
    private Long userId; // ID of the user who performed the action (optional)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
