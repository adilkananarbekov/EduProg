package com.edu.edupage.config;

import com.edu.edupage.entity.*;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

        private final UserRepository userRepository;
        private final StudentRepository studentRepository;
        private final TeacherRepository teacherRepository;
        private final ClassGroupRepository classGroupRepository;
        private final SubjectRepository subjectRepository;
        private final GradeRepository gradeRepository;
        private final ScheduleRepository scheduleRepository;
        private final AttendanceRepository attendanceRepository;
        private final AnnouncementRepository announcementRepository;
        private final PasswordEncoder passwordEncoder;

        private final Random random = new Random(42); // Fixed seed for reproducibility

        @Override
        public void run(String... args) {
                if (userRepository.count() > 0) {
                        log.info("Database already seeded, skipping...");
                        return;
                }

                log.info("Seeding database with comprehensive sample data...");

                // Create subjects
                List<Subject> subjects = createSubjects();
                
                // Create class groups
                List<ClassGroup> classGroups = createClassGroups();
                
                // Create admin
                User adminUser = createAdmin();
                
                // Create teachers
                List<Teacher> teachers = createTeachers(subjects);
                
                // Create students
                List<Student> students = createStudents(classGroups);
                
                // Create schedules
                List<Schedule> schedules = createSchedules(classGroups, teachers, subjects);
                
                // Create grades
                createGrades(students, subjects, teachers);
                
                // Create attendance records
                createAttendance(students, schedules, teachers);
                
                // Create announcements
                createAnnouncements(adminUser, teachers, classGroups);

                log.info("========================================");
                log.info("Database seeded successfully!");
                log.info("========================================");
                log.info("Admin: admin@edupage.com / admin123");
                log.info("Teachers: teacher1@edupage.com ... teacher6@edupage.com / teacher123");
                log.info("Students: student1@edupage.com ... student25@edupage.com / student123");
                log.info("========================================");
        }

        private List<Subject> createSubjects() {
                List<Subject> subjects = new ArrayList<>();
                
                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Mathematics")
                        .description("Algebra, Geometry, and Calculus")
                        .hoursPerWeek(5)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Physics")
                        .description("Mechanics, Thermodynamics, and Electromagnetism")
                        .hoursPerWeek(4)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Chemistry")
                        .description("Organic and Inorganic Chemistry")
                        .hoursPerWeek(3)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Biology")
                        .description("Cell Biology, Genetics, and Ecology")
                        .hoursPerWeek(3)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("English Literature")
                        .description("Classic and Modern Literature Analysis")
                        .hoursPerWeek(4)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("History")
                        .description("World History and Civilizations")
                        .hoursPerWeek(3)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Computer Science")
                        .description("Programming, Algorithms, and Data Structures")
                        .hoursPerWeek(4)
                        .build()));

                subjects.add(subjectRepository.save(Subject.builder()
                        .name("Physical Education")
                        .description("Sports and Fitness Training")
                        .hoursPerWeek(2)
                        .build()));

                return subjects;
        }

        private List<ClassGroup> createClassGroups() {
                List<ClassGroup> groups = new ArrayList<>();
                
                String[] sections = {"A", "B", "C"};
                for (int grade = 9; grade <= 12; grade++) {
                        for (String section : sections) {
                                if (grade == 12 && section.equals("C")) continue; // Skip 12C
                                groups.add(classGroupRepository.save(ClassGroup.builder()
                                        .name(grade + section)
                                        .grade(grade)
                                        .build()));
                        }
                }
                return groups;
        }

        private User createAdmin() {
                return userRepository.save(User.builder()
                        .email("admin@edupage.com")
                        .password(passwordEncoder.encode("admin123"))
                        .firstName("Admin")
                        .lastName("User")
                        .role(Role.ADMIN)
                        .build());
        }

        private List<Teacher> createTeachers(List<Subject> subjects) {
                List<Teacher> teachers = new ArrayList<>();
                
                String[][] teacherData = {
                        {"John", "Smith", "Mathematics", "Physics"},
                        {"Sarah", "Johnson", "Chemistry", "Biology"},
                        {"Michael", "Williams", "English Literature", "History"},
                        {"Emily", "Brown", "Computer Science", "Mathematics"},
                        {"David", "Jones", "Physical Education"},
                        {"Jennifer", "Davis", "Physics", "Chemistry"}
                };

                for (int i = 0; i < teacherData.length; i++) {
                        String[] data = teacherData[i];
                        User teacherUser = userRepository.save(User.builder()
                                .email("teacher" + (i + 1) + "@edupage.com")
                                .password(passwordEncoder.encode("teacher123"))
                                .firstName(data[0])
                                .lastName(data[1])
                                .role(Role.TEACHER)
                                .build());

                        Set<Subject> teacherSubjects = new HashSet<>();
                        for (int j = 2; j < data.length; j++) {
                                String subjectName = data[j];
                                subjects.stream()
                                        .filter(s -> s.getName().equals(subjectName))
                                        .findFirst()
                                        .ifPresent(teacherSubjects::add);
                        }

                        teachers.add(teacherRepository.save(Teacher.builder()
                                .user(teacherUser)
                                .subjects(teacherSubjects)
                                .employeeNumber("T" + String.format("%03d", i + 1))
                                .build()));
                }
                return teachers;
        }

        private List<Student> createStudents(List<ClassGroup> classGroups) {
                List<Student> students = new ArrayList<>();
                
                String[][] studentNames = {
                        {"Alice", "Anderson"}, {"Bob", "Baker"}, {"Charlie", "Clark"},
                        {"Diana", "Davis"}, {"Edward", "Evans"}, {"Fiona", "Foster"},
                        {"George", "Garcia"}, {"Hannah", "Harris"}, {"Ivan", "Ivanov"},
                        {"Julia", "Jackson"}, {"Kevin", "King"}, {"Laura", "Lee"},
                        {"Marcus", "Martinez"}, {"Nina", "Nelson"}, {"Oscar", "Ortiz"},
                        {"Patricia", "Patel"}, {"Quinn", "Quinn"}, {"Rachel", "Roberts"},
                        {"Samuel", "Smith"}, {"Tina", "Thompson"}, {"Ulrich", "Underwood"},
                        {"Victoria", "Valdez"}, {"William", "Wilson"}, {"Xena", "Xavier"},
                        {"Yusuf", "Young"}
                };

                int studentNum = 1;
                for (int i = 0; i < studentNames.length; i++) {
                        String[] name = studentNames[i];
                        ClassGroup classGroup = classGroups.get(i % classGroups.size());
                        
                        User studentUser = userRepository.save(User.builder()
                                .email("student" + studentNum + "@edupage.com")
                                .password(passwordEncoder.encode("student123"))
                                .firstName(name[0])
                                .lastName(name[1])
                                .role(Role.STUDENT)
                                .build());

                        students.add(studentRepository.save(Student.builder()
                                .user(studentUser)
                                .classGroup(classGroup)
                                .studentNumber("S" + String.format("%04d", studentNum))
                                .build()));
                        
                        studentNum++;
                }
                return students;
        }

        private List<Schedule> createSchedules(List<ClassGroup> classGroups, List<Teacher> teachers, List<Subject> subjects) {
                List<Schedule> schedules = new ArrayList<>();
                String[] rooms = {"101", "102", "103", "201", "202", "203", "Lab 1", "Lab 2", "Gym"};
                
                for (ClassGroup classGroup : classGroups) {
                        int lessonNum = 1;
                        for (DayOfWeek day : Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                                // 4-5 lessons per day
                                int lessonsToday = 4 + random.nextInt(2);
                                LocalTime startTime = LocalTime.of(8, 0);
                                
                                for (int lesson = 0; lesson < lessonsToday; lesson++) {
                                        Subject subject = subjects.get(random.nextInt(subjects.size()));
                                        Teacher teacher = findTeacherForSubject(teachers, subject);
                                        
                                        if (teacher != null) {
                                                schedules.add(scheduleRepository.save(Schedule.builder()
                                                        .classGroup(classGroup)
                                                        .teacher(teacher)
                                                        .subject(subject)
                                                        .dayOfWeek(day)
                                                        .startTime(startTime)
                                                        .endTime(startTime.plusMinutes(45))
                                                        .room(rooms[random.nextInt(rooms.length)])
                                                        .lessonNumber(lessonNum++)
                                                        .build()));
                                        }
                                        startTime = startTime.plusMinutes(55); // 45 min class + 10 min break
                                }
                        }
                }
                return schedules;
        }

        private Teacher findTeacherForSubject(List<Teacher> teachers, Subject subject) {
                for (Teacher teacher : teachers) {
                        if (teacher.getSubjects().contains(subject)) {
                                return teacher;
                        }
                }
                return teachers.get(0); // Fallback
        }

        private void createGrades(List<Student> students, List<Subject> subjects, List<Teacher> teachers) {
                String[] gradeTypes = {"Homework", "Quiz", "Test", "Exam", "Project", "Participation"};
                LocalDate startDate = LocalDate.now().minusMonths(3);
                
                for (Student student : students) {
                        for (Subject subject : subjects) {
                                Teacher teacher = findTeacherForSubject(teachers, subject);
                                if (teacher == null) continue;
                                
                                // Create 5-10 grades per student per subject
                                int numGrades = 5 + random.nextInt(6);
                                for (int i = 0; i < numGrades; i++) {
                                        String gradeType = gradeTypes[random.nextInt(gradeTypes.length)];
                                        double maxValue = gradeType.equals("Exam") ? 100.0 : 
                                                         gradeType.equals("Test") ? 50.0 : 
                                                         gradeType.equals("Quiz") ? 20.0 : 10.0;
                                        
                                        // Generate realistic grade distribution (60-100 range mostly)
                                        double value = maxValue * (0.6 + random.nextDouble() * 0.4);
                                        value = Math.round(value * 10.0) / 10.0; // Round to 1 decimal
                                        
                                        gradeRepository.save(Grade.builder()
                                                .student(student)
                                                .subject(subject)
                                                .teacher(teacher)
                                                .value(value)
                                                .maxValue(maxValue)
                                                .gradeType(gradeType)
                                                .description(gradeType + " - " + subject.getName())
                                                .date(startDate.plusDays(random.nextInt(90)))
                                                .build());
                                }
                        }
                }
        }

        private void createAttendance(List<Student> students, List<Schedule> schedules, List<Teacher> teachers) {
                AttendanceStatus[] statuses = AttendanceStatus.values();
                LocalDate startDate = LocalDate.now().minusWeeks(4);
                
                // Create attendance for the last 4 weeks
                for (int week = 0; week < 4; week++) {
                        LocalDate weekStart = startDate.plusWeeks(week);
                        
                        for (Schedule schedule : schedules) {
                                // Skip if not the correct day
                                LocalDate attendanceDate = weekStart;
                                while (attendanceDate.getDayOfWeek() != schedule.getDayOfWeek()) {
                                        attendanceDate = attendanceDate.plusDays(1);
                                }
                                
                                // Get students in this class
                                List<Student> classStudents = students.stream()
                                        .filter(s -> s.getClassGroup().equals(schedule.getClassGroup()))
                                        .toList();
                                
                                User markedBy = schedule.getTeacher().getUser();
                                
                                for (Student student : classStudents) {
                                        // 90% present, 5% late, 3% absent, 2% excused
                                        int roll = random.nextInt(100);
                                        AttendanceStatus status;
                                        if (roll < 90) status = AttendanceStatus.PRESENT;
                                        else if (roll < 95) status = AttendanceStatus.LATE;
                                        else if (roll < 98) status = AttendanceStatus.ABSENT;
                                        else status = AttendanceStatus.EXCUSED;
                                        
                                        attendanceRepository.save(Attendance.builder()
                                                .student(student)
                                                .schedule(schedule)
                                                .date(attendanceDate)
                                                .status(status)
                                                .notes(status != AttendanceStatus.PRESENT ? 
                                                        "Auto-generated sample data" : null)
                                                .markedBy(markedBy)
                                                .build());
                                }
                        }
                }
        }

        private void createAnnouncements(User admin, List<Teacher> teachers, List<ClassGroup> classGroups) {
                // School-wide announcements from admin
                announcementRepository.save(Announcement.builder()
                        .title("Welcome Back to School!")
                        .content("Dear students and staff, welcome back to the new semester! We hope you had a wonderful break. Classes begin as scheduled. Please check the updated timetable on the school website.")
                        .author(admin)
                        .targetRole(null) // For everyone
                        .important(true)
                        .publishedAt(LocalDateTime.now().minusDays(7))
                        .build());

                announcementRepository.save(Announcement.builder()
                        .title("Parent-Teacher Meeting Scheduled")
                        .content("The quarterly parent-teacher meeting is scheduled for next Friday at 5 PM. All parents are encouraged to attend. Teachers will be available in their respective classrooms.")
                        .author(admin)
                        .targetRole(null)
                        .important(true)
                        .publishedAt(LocalDateTime.now().minusDays(3))
                        .expiresAt(LocalDateTime.now().plusDays(10))
                        .build());

                announcementRepository.save(Announcement.builder()
                        .title("Library Hours Extended")
                        .content("The school library will now be open until 6 PM on weekdays. Students preparing for exams are encouraged to utilize this resource.")
                        .author(admin)
                        .targetRole(Role.STUDENT)
                        .important(false)
                        .publishedAt(LocalDateTime.now().minusDays(1))
                        .build());

                // Teacher announcements
                if (!teachers.isEmpty()) {
                        User teacherUser = teachers.get(0).getUser();
                        
                        announcementRepository.save(Announcement.builder()
                                .title("Mathematics Quiz Reminder")
                                .content("Reminder: There will be a quiz on Chapter 5 (Quadratic Equations) this Thursday. Please review your notes and practice problems.")
                                .author(teacherUser)
                                .targetClassGroup(classGroups.get(0))
                                .important(true)
                                .publishedAt(LocalDateTime.now().minusDays(2))
                                .build());

                        announcementRepository.save(Announcement.builder()
                                .title("Homework Submission Deadline")
                                .content("All pending homework assignments must be submitted by Friday. Late submissions will receive a 10% penalty. If you have any issues, please speak to me during office hours.")
                                .author(teacherUser)
                                .targetRole(Role.STUDENT)
                                .important(false)
                                .publishedAt(LocalDateTime.now())
                                .build());
                }

                if (teachers.size() > 2) {
                        User scienceTeacher = teachers.get(1).getUser();
                        
                        announcementRepository.save(Announcement.builder()
                                .title("Science Lab Safety Reminder")
                                .content("All students must wear lab coats and safety goggles during chemistry experiments. Please arrive on time for lab sessions.")
                                .author(scienceTeacher)
                                .targetRole(Role.STUDENT)
                                .important(true)
                                .publishedAt(LocalDateTime.now().minusHours(5))
                                .build());
                }
        }
}
