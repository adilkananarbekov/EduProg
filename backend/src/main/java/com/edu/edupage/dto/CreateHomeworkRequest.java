package com.edu.edupage.dto;

import com.edu.edupage.entity.HomeworkType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomeworkRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Subject is required")
    private Long subjectId;
    
    @NotNull(message = "Class group is required")
    private Long classGroupId;
    
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be in the future")
    private LocalDate dueDate;
    
    @Positive(message = "Max score must be positive")
    private Integer maxScore;
    
    @NotNull(message = "Homework type is required")
    private HomeworkType type;
}
