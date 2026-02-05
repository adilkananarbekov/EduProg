package com.edu.edupage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id;
    private Long classGroupId;
    private String classGroupName;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private String subjectShortName; // Short name for compact UI display
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private Integer lessonNumber;
}
