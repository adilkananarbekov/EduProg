package com.edu.edupage.dto;

import com.edu.edupage.entity.ExamType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateExamRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Subject is required")
    private Long subjectId;
    
    @NotNull(message = "Class group is required")
    private Long classGroupId;
    
    @NotNull(message = "Exam date is required")
    @FutureOrPresent(message = "Exam date must be in the future")
    private LocalDate examDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String room;
    
    @Positive(message = "Max score must be positive")
    private Integer maxScore;
    
    @Positive(message = "Duration must be positive")
    private Integer duration;
    
    @NotNull(message = "Exam type is required")
    private ExamType type;
}
