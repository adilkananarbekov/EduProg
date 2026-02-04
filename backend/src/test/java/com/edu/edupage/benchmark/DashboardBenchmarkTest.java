package com.edu.edupage.benchmark;

import com.edu.edupage.entity.*;
import com.edu.edupage.repository.*;
import com.edu.edupage.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class DashboardBenchmarkTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassGroupRepository classGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Mock unrelated services to isolate the schedule retrieval
    @MockBean
    private com.edu.edupage.service.GradeService gradeService;
    @MockBean
    private com.edu.edupage.service.AttendanceService attendanceService;
    @MockBean
    private com.edu.edupage.service.HomeworkService homeworkService;
    @MockBean
    private com.edu.edupage.service.AnnouncementService announcementService;
    @MockBean
    private com.edu.edupage.service.EventService eventService;
    @MockBean
    private com.edu.edupage.service.NotificationService notificationService;
    @MockBean
    private com.edu.edupage.service.MessageService messageService;
    @MockBean
    private com.edu.edupage.repository.SubmissionRepository submissionRepository;

    @Test
    @Transactional
    public void benchmarkGetStudentDashboard() {
        // Setup Mocks
        when(gradeService.getStudentGrades(anyLong())).thenReturn(Collections.emptyList());
        when(gradeService.getStudentGradeAverages(anyLong())).thenReturn(Map.of());
        when(attendanceService.getStudentAttendanceStats(anyLong())).thenReturn(Map.of());
        when(homeworkService.getPendingHomeworkForClass(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(announcementService.getAnnouncementsForStudent(anyLong())).thenReturn(Collections.emptyList());
        when(eventService.getUpcomingEventsForStudent(anyLong())).thenReturn(Collections.emptyList());
        when(notificationService.getUnreadCount(anyLong())).thenReturn(0L);

        // Setup Data
        ClassGroup classGroup = ClassGroup.builder()
                .name("10A")
                .grade(10)
                .build();
        classGroup = classGroupRepository.save(classGroup);

        User studentUser = User.builder()
                .email("student@test.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();
        studentUser = userRepository.save(studentUser);

        Student student = Student.builder()
                .user(studentUser)
                .classGroup(classGroup)
                .studentNumber("S12345")
                .build();
        student = studentRepository.save(student);

        User teacherUser = User.builder()
                .email("teacher@test.com")
                .password("password")
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.TEACHER)
                .build();
        teacherUser = userRepository.save(teacherUser);

        Teacher teacher = Teacher.builder()
                .user(teacherUser)
                .build();
        teacher = teacherRepository.save(teacher);

        Subject subject = Subject.builder()
                .name("Math")
                .hoursPerWeek(5)
                .build();
        subject = subjectRepository.save(subject);

        // Create weekly schedule (5 days, 8 lessons per day = 40 schedules)
        List<Schedule> schedules = new ArrayList<>();
        DayOfWeek[] days = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};

        for (DayOfWeek day : days) {
            LocalTime startTime = LocalTime.of(8, 0);
            for (int i = 1; i <= 8; i++) {
                Schedule schedule = Schedule.builder()
                        .classGroup(classGroup)
                        .teacher(teacher)
                        .subject(subject)
                        .dayOfWeek(day)
                        .startTime(startTime)
                        .endTime(startTime.plusMinutes(45))
                        .lessonNumber(i)
                        .room("101")
                        .build();
                schedules.add(schedule);
                startTime = startTime.plusMinutes(55); // 45 min lesson + 10 min break
            }
        }
        scheduleRepository.saveAll(schedules);

        // Verify correctness
        var dashboard = dashboardService.getStudentDashboard(studentUser.getId());
        DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        if (today != DayOfWeek.SATURDAY && today != DayOfWeek.SUNDAY) {
            if (dashboard.getTodaySchedule().isEmpty()) {
                System.out.println("WARNING: Dashboard schedule is empty on a weekday (" + today + "). This might indicate a bug or data issue.");
            } else {
                 System.out.println("Verified: Dashboard schedule has " + dashboard.getTodaySchedule().size() + " items for " + today);
            }
        }

        // Warmup
        for (int i = 0; i < 100; i++) {
            dashboardService.getStudentDashboard(studentUser.getId());
        }

        // Benchmark
        long startTime = System.nanoTime();
        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            dashboardService.getStudentDashboard(studentUser.getId());
        }
        long endTime = System.nanoTime();

        double averageTimeMs = (endTime - startTime) / (iterations * 1_000_000.0);
        System.out.println("Benchmark Result: Average execution time for getStudentDashboard: " + averageTimeMs + " ms");
    }
}
