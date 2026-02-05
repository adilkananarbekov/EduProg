package com.edu.edupage.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;
    private String name;
    private String description;
    private Integer hoursPerWeek;
}
