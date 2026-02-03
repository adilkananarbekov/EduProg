package com.edu.edupage.dto;

import com.edu.edupage.entity.EventType;
import com.edu.edupage.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private EventType type;
    private String createdByName;
    private Role targetRole;
    private Long targetClassGroupId;
    private String targetClassGroupName;
    private Boolean isPublic;
    private LocalDateTime createdAt;
}
