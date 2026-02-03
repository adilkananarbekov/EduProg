package com.edu.edupage.dto;

import com.edu.edupage.entity.ExamType;
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
public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long classGroupId;
    private String classGroupName;
    private Long teacherId;
    private String teacherName;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private Integer maxScore;
    private Integer duration;
    private ExamType type;
    private LocalDateTime createdAt;
}
