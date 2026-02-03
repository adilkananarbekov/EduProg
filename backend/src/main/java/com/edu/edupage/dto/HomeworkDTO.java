package com.edu.edupage.dto;

import com.edu.edupage.entity.HomeworkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDTO {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long classGroupId;
    private String classGroupName;
    private Long teacherId;
    private String teacherName;
    private LocalDate dueDate;
    private Integer maxScore;
    private HomeworkType type;
    private int totalSubmissions;
    private int gradedSubmissions;
    private boolean isSubmitted; // For student view
    private LocalDateTime createdAt;
}
