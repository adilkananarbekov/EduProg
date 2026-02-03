package com.edu.edupage.dto;

import com.edu.edupage.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private Long id;
    private Long homeworkId;
    private String homeworkTitle;
    private Long studentId;
    private String studentName;
    private String content;
    private String attachmentUrl;
    private LocalDateTime submittedAt;
    private Integer score;
    private Integer maxScore;
    private String feedback;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private SubmissionStatus status;
}
