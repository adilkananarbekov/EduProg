package com.edu.edupage.service;

import com.edu.edupage.dto.*;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleService scheduleService;
    private final GradeService gradeService;
    private final AttendanceService attendanceService;
    private final HomeworkService homeworkService;
    private final AnnouncementService announcementService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final ScheduleRepository scheduleRepository;
    private final SubmissionRepository submissionRepository;

    public StudentDashboardDTO getStudentDashboard(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Long classGroupId = student.getClassGroup().getId();
        Long studentId = student.getId();

        // Today's schedule
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ScheduleDTO> todaySchedule = scheduleService.getWeeklyScheduleForClass(classGroupId)
                .stream()
                .filter(s -> s.getDayOfWeek().equals(today.toString()))
                .collect(Collectors.toList());

        // Upcoming homework
        List<HomeworkDTO> upcomingHomework = homeworkService.getPendingHomeworkForClass(classGroupId, studentId);
        int pendingCount = (int) upcomingHomework.stream().filter(h -> !h.isSubmitted()).count();

        // Recent grades (last 5)
        List<GradeDTO> recentGrades = gradeService.getStudentGrades(studentId);
        if (recentGrades.size() > 5) {
            recentGrades = recentGrades.subList(0, 5);
        }

        // Attendance stats
        Map<String, Long> attendanceStats = attendanceService.getStudentAttendanceStats(studentId);
        long total = attendanceStats.values().stream().mapToLong(Long::longValue).sum();
        long present = attendanceStats.getOrDefault("PRESENT", 0L);
        Double attendancePercentage = total > 0 ? (double) present / total * 100 : 100.0;

        // Grade averages
        Map<String, Double> gradeAverages = gradeService.getStudentGradeAverages(studentId);
        Double overallAverage = gradeAverages.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Notifications
        int unreadNotifications = (int) notificationService.getUnreadCount(userId);

        // Recent announcements (last 3)
        List<AnnouncementDTO> recentAnnouncements = announcementService.getAnnouncementsForStudent(studentId);
        if (recentAnnouncements.size() > 3) {
            recentAnnouncements = recentAnnouncements.subList(0, 3);
        }

        // Upcoming events (next 5)
        List<EventDTO> upcomingEvents = eventService.getUpcomingEventsForStudent(studentId);
        if (upcomingEvents.size() > 5) {
            upcomingEvents = upcomingEvents.subList(0, 5);
        }

        return StudentDashboardDTO.builder()
                .todaySchedule(todaySchedule)
                .upcomingHomework(upcomingHomework)
                .pendingHomeworkCount(pendingCount)
                .recentGrades(recentGrades)
                .attendanceStats(attendanceStats)
                .attendancePercentage(Math.round(attendancePercentage * 10) / 10.0)
                .gradeAverages(gradeAverages)
                .overallAverage(Math.round(overallAverage * 10) / 10.0)
                .unreadNotifications(unreadNotifications)
                .recentAnnouncements(recentAnnouncements)
                .upcomingEvents(upcomingEvents)
                .build();
    }

    public TeacherDashboardDTO getTeacherDashboard(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        Long teacherId = teacher.getId();

        // Today's schedule
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ScheduleDTO> todaySchedule = scheduleService.getWeeklyScheduleForTeacher(teacherId)
                .stream()
                .filter(s -> s.getDayOfWeek().equals(today.toString()))
                .collect(Collectors.toList());

        // Classes info
        List<Schedule> teacherSchedules = scheduleRepository.findByTeacherId(teacherId);
        Map<String, TeacherDashboardDTO.ClassInfoDTO> classMap = new HashMap<>();
        
        for (Schedule schedule : teacherSchedules) {
            String key = schedule.getClassGroup().getId() + "-" + schedule.getSubject().getId();
            if (!classMap.containsKey(key)) {
                int studentCount = studentRepository.countByClassGroupId(schedule.getClassGroup().getId());
                classMap.put(key, TeacherDashboardDTO.ClassInfoDTO.builder()
                        .classGroupId(schedule.getClassGroup().getId())
                        .classGroupName(schedule.getClassGroup().getName())
                        .subjectName(schedule.getSubject().getName())
                        .studentCount(studentCount)
                        .build());
            }
        }
        List<TeacherDashboardDTO.ClassInfoDTO> myClasses = new ArrayList<>(classMap.values());
        int totalStudents = myClasses.stream().mapToInt(TeacherDashboardDTO.ClassInfoDTO::getStudentCount).sum();

        // Pending submissions to grade
        List<HomeworkDTO> recentHomework = homeworkService.getPendingHomeworkByTeacher(teacherId);
        int pendingSubmissions = 0;
        for (HomeworkDTO hw : recentHomework) {
            pendingSubmissions += (hw.getTotalSubmissions() - hw.getGradedSubmissions());
        }

        // Recent announcements
        List<AnnouncementDTO> recentAnnouncements = announcementService.getAnnouncementsForTeacher();
        if (recentAnnouncements.size() > 3) {
            recentAnnouncements = recentAnnouncements.subList(0, 3);
        }

        // Upcoming events
        List<EventDTO> upcomingEvents = eventService.getUpcomingEventsForTeacher();
        if (upcomingEvents.size() > 5) {
            upcomingEvents = upcomingEvents.subList(0, 5);
        }

        // Unread counts
        int unreadMessages = (int) messageService.getUnreadCount(userId);
        int unreadNotifications = (int) notificationService.getUnreadCount(userId);

        return TeacherDashboardDTO.builder()
                .todaySchedule(todaySchedule)
                .myClasses(myClasses)
                .totalStudents(totalStudents)
                .pendingSubmissions(pendingSubmissions)
                .recentHomework(recentHomework)
                .recentAnnouncements(recentAnnouncements)
                .upcomingEvents(upcomingEvents)
                .unreadMessages(unreadMessages)
                .unreadNotifications(unreadNotifications)
                .build();
    }
}
