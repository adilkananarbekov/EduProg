package com.edu.edupage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {
    // Today's schedule
    private List<ScheduleDTO> todaySchedule;
    
    // Upcoming homework
    private List<HomeworkDTO> upcomingHomework;
    private int pendingHomeworkCount;
    
    // Recent grades
    private List<GradeDTO> recentGrades;
    
    // Attendance summary
    private Map<String, Long> attendanceStats;
    private Double attendancePercentage;
    
    // Grade averages by subject
    private Map<String, Double> gradeAverages;
    private Double overallAverage;
    
    // Unread notifications
    private int unreadNotifications;
    
    // Recent announcements
    private List<AnnouncementDTO> recentAnnouncements;
    
    // Upcoming events
    private List<EventDTO> upcomingEvents;
}
