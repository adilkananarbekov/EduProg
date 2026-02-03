package com.edu.edupage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitHomeworkRequest {
    @NotNull(message = "Homework ID is required")
    private Long homeworkId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String attachmentUrl;
}
