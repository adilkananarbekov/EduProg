package com.edu.edupage.dto;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalCourseDTO {
    private Long id;
    private String name;
    private String description;
    private String instructor;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private Integer maxCapacity;
    private Integer enrolledCount;
}
