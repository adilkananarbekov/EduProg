package com.edu.edupage.dto;

import com.edu.edupage.entity.EventType;
import com.edu.edupage.entity.Role;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must be in the future")
    private LocalDate eventDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String location;
    
    @NotNull(message = "Event type is required")
    private EventType type;
    
    private Role targetRole; // null means for everyone
    
    private Long targetClassGroupId; // null means for all classes
    
    private Boolean isPublic;
}
