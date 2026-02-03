package com.edu.edupage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardDTO {
    // Today's schedule
    private List<ScheduleDTO> todaySchedule;
    
    // Classes info
    private List<ClassInfoDTO> myClasses;
    private int totalStudents;
    
    // Pending grading
    private int pendingSubmissions;
    private List<HomeworkDTO> recentHomework;
    
    // Recent announcements
    private List<AnnouncementDTO> recentAnnouncements;
    
    // Upcoming events
    private List<EventDTO> upcomingEvents;
    
    // Unread messages
    private int unreadMessages;
    
    // Unread notifications
    private int unreadNotifications;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassInfoDTO {
        private Long classGroupId;
        private String classGroupName;
        private String subjectName;
        private int studentCount;
    }
}
