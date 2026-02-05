package com.edu.edupage.service;

import com.edu.edupage.dto.CreateScheduleRequest;
import com.edu.edupage.dto.GenerateScheduleRequest;
import com.edu.edupage.dto.ScheduleDTO;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

        private final ScheduleRepository scheduleRepository;
        private final ClassGroupRepository classGroupRepository;
        private final TeacherRepository teacherRepository;
        private final SubjectRepository subjectRepository;
        private final ClassroomRepository classroomRepository;
        private final StudentRepository studentRepository;

        @Transactional
        public List<ScheduleDTO> getStudentSchedule(Student detachedStudent) {
                // Re-fetch student to ensure we have an attached entity with an open session
                // for lazy loading
                Student student = studentRepository.findById(detachedStudent.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Student", "id",
                                                detachedStudent.getId()));

                List<ScheduleDTO> allSchedules = new ArrayList<>();

                // 1. Primary Class Group
                if (student.getClassGroup() != null) {
                        allSchedules.addAll(getWeeklyScheduleForClass(student.getClassGroup().getId()));
                }

                // 2. Additional Class Groups
                // Accessing this lazy collection within @Transactional should work if the
                // session is open
                if (student.getAdditionalClassGroups() != null) {
                        // Force initialization if needed, or just iterate
                        for (ClassGroup group : student.getAdditionalClassGroups()) {
                                allSchedules.addAll(getWeeklyScheduleForClass(group.getId()));
                        }
                }

                // 3. Additional Courses (Mapping AdditionalCourse to ScheduleDTO)
                if (student.getAdditionalCourses() != null) {
                        for (AdditionalCourse course : student.getAdditionalCourses()) {
                                allSchedules.add(mapCourseToDTO(course));
                        }
                }

                return allSchedules;
        }

        private ScheduleDTO mapCourseToDTO(AdditionalCourse course) {
                return ScheduleDTO.builder()
                                .id(course.getId() * -1) // Negative ID to distinguish from regular schedules?? Or just
                                                         // use unique logic.
                                .subjectName(course.getName())
                                .teacherName(course.getInstructor() != null ? course.getInstructor() : "Instructor")
                                .dayOfWeek(course.getDayOfWeek() != null ? course.getDayOfWeek() : DayOfWeek.MONDAY) // Default
                                                                                                                     // to
                                                                                                                     // MONDAY
                                .startTime(course.getStartTime())
                                .endTime(course.getEndTime())
                                .room(course.getRoom())
                                .lessonNumber(0) // Special value
                                .build();
        }

        public List<ScheduleDTO> getWeeklyScheduleForClass(Long classGroupId) {
                return scheduleRepository.findWeeklyScheduleByClassGroup(classGroupId)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<ScheduleDTO> getWeeklyScheduleForTeacher(Long teacherId) {
                return scheduleRepository.findWeeklyScheduleByTeacher(teacherId)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<ScheduleDTO> getWeeklyScheduleForClassroom(Long classroomId) {
                return scheduleRepository.findByClassroom_Id(classroomId)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<ScheduleDTO> getAllSchedules() {
                return scheduleRepository.findAll()
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public ScheduleDTO createSchedule(CreateScheduleRequest request) {
                // Validate no conflicts
                // Validate no conflicts
                validateNoConflicts(request.getTeacherId(), request.getClassGroupId(),
                                request.getDayOfWeek(), request.getStartTime(), request.getEndTime(),
                                request.getRoom(), null);

                ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id",
                                                request.getClassGroupId()));

                Teacher teacher = teacherRepository.findById(request.getTeacherId())
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id",
                                                request.getTeacherId()));

                Subject subject = subjectRepository.findById(request.getSubjectId())
                                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id",
                                                request.getSubjectId()));

                Schedule schedule = Schedule.builder()
                                .classGroup(classGroup)
                                .teacher(teacher)
                                .subject(subject)
                                .dayOfWeek(request.getDayOfWeek())
                                .startTime(request.getStartTime())
                                .endTime(request.getEndTime())
                                .endTime(request.getEndTime())
                                .lessonNumber(request.getLessonNumber())
                                .build();

                if (request.getRoom() != null) {
                        Classroom classroom = classroomRepository.findByRoomNumber(request.getRoom())
                                        .orElseThrow(() -> new ResourceNotFoundException("Classroom", "number",
                                                        request.getRoom()));
                        schedule.setClassroom(classroom);
                }

                schedule = scheduleRepository.save(schedule);
                return mapToDTO(schedule);
        }

        @Transactional
        public List<ScheduleDTO> generateSchedule(GenerateScheduleRequest request) {
                List<Schedule> generatedSchedules = new ArrayList<>();
                List<Classroom> allClassrooms = classroomRepository.findAll();

                LocalTime dayStart = request.getDayStartTime() != null ? request.getDayStartTime() : LocalTime.of(8, 0);
                LocalTime dayEnd = request.getDayEndTime() != null ? request.getDayEndTime() : LocalTime.of(15, 0);
                int lessonDuration = request.getLessonDurationMinutes() != null ? request.getLessonDurationMinutes()
                                : 45;
                int breakDuration = request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 15;

                DayOfWeek[] weekDays = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

                for (GenerateScheduleRequest.TeacherSubjectMapping mapping : request.getTeacherSubjectMappings()) {
                        Teacher teacher = teacherRepository.findById(mapping.getTeacherId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id",
                                                        mapping.getTeacherId()));

                        Subject subject = subjectRepository.findById(mapping.getSubjectId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Subject", "id",
                                                        mapping.getSubjectId()));

                        for (Long classGroupId : mapping.getClassGroupIds()) {
                                ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                                                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id",
                                                                classGroupId));

                                int lessonsNeeded = subject.getHoursPerWeek();
                                int lessonsScheduled = 0;

                                outerLoop: for (DayOfWeek day : weekDays) {
                                        LocalTime currentTime = dayStart;
                                        int lessonNumber = 1;

                                        while (currentTime.plusMinutes(lessonDuration).isBefore(dayEnd) ||
                                                        currentTime.plusMinutes(lessonDuration).equals(dayEnd)) {

                                                LocalTime endTime = currentTime.plusMinutes(lessonDuration);

                                                // Check for conflicts
                                                List<Schedule> teacherConflicts = scheduleRepository
                                                                .findConflictingTeacherSchedules(
                                                                                teacher.getId(), day, currentTime,
                                                                                endTime);
                                                List<Schedule> classConflicts = scheduleRepository
                                                                .findConflictingClassSchedules(
                                                                                classGroup.getId(), day, currentTime,
                                                                                endTime);

                                                if (teacherConflicts.isEmpty() && classConflicts.isEmpty()) {
                                                        // Find available classroom
                                                        Classroom appointedRoom = null;
                                                        for (Classroom room : allClassrooms) {
                                                                List<Schedule> roomConflicts = scheduleRepository
                                                                                .findConflictingClassroomSchedules(
                                                                                                room.getId(), day,
                                                                                                currentTime, endTime);
                                                                if (roomConflicts.isEmpty()) {
                                                                        appointedRoom = room;
                                                                        break;
                                                                }
                                                        }

                                                        if (appointedRoom != null) {
                                                                Schedule schedule = Schedule.builder()
                                                                                .classGroup(classGroup)
                                                                                .teacher(teacher)
                                                                                .subject(subject)
                                                                                .dayOfWeek(day)
                                                                                .startTime(currentTime)
                                                                                .endTime(endTime)
                                                                                .classroom(appointedRoom)
                                                                                .lessonNumber(lessonNumber)
                                                                                .build();

                                                                schedule = scheduleRepository.save(schedule);
                                                                generatedSchedules.add(schedule);
                                                                lessonsScheduled++;

                                                                if (lessonsScheduled >= lessonsNeeded) {
                                                                        break outerLoop;
                                                                }
                                                        }
                                                }

                                                currentTime = currentTime.plusMinutes(lessonDuration + breakDuration);
                                                lessonNumber++;
                                        }
                                }
                        }
                }

                return generatedSchedules.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public void deleteSchedule(Long id) {
                if (!scheduleRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Schedule", "id", id);
                }
                scheduleRepository.deleteById(id);
        }

        private void validateNoConflicts(Long teacherId, Long classGroupId, DayOfWeek day,
                        LocalTime startTime, LocalTime endTime, String roomNumber, Long excludeId) {
                List<Schedule> teacherConflicts = scheduleRepository.findConflictingTeacherSchedules(
                                teacherId, day, startTime, endTime);

                if (excludeId != null) {
                        teacherConflicts = teacherConflicts.stream()
                                        .filter(s -> !s.getId().equals(excludeId))
                                        .collect(Collectors.toList());
                }

                if (!teacherConflicts.isEmpty()) {
                        throw new IllegalArgumentException("Teacher has a conflicting schedule at this time");
                }

                List<Schedule> classConflicts = scheduleRepository.findConflictingClassSchedules(
                                classGroupId, day, startTime, endTime);

                if (excludeId != null) {
                        classConflicts = classConflicts.stream()
                                        .filter(s -> !s.getId().equals(excludeId))
                                        .collect(Collectors.toList());
                }

                if (!classConflicts.isEmpty()) {
                        throw new IllegalArgumentException("Class has a conflicting schedule at this time");
                }

                if (roomNumber != null) {
                        Classroom classroom = classroomRepository.findByRoomNumber(roomNumber)
                                        .orElseThrow(() -> new ResourceNotFoundException("Classroom", "number",
                                                        roomNumber));

                        List<Schedule> roomConflicts = scheduleRepository.findConflictingClassroomSchedules(
                                        classroom.getId(), day, startTime, endTime);

                        if (excludeId != null) {
                                roomConflicts = roomConflicts.stream()
                                                .filter(s -> !s.getId().equals(excludeId))
                                                .collect(Collectors.toList());
                        }

                        if (!roomConflicts.isEmpty()) {
                                throw new IllegalArgumentException(
                                                "Room " + roomNumber + " is already occupied at this time");
                        }
                }
        }

        private ScheduleDTO mapToDTO(Schedule schedule) {
                return ScheduleDTO.builder()
                                .id(schedule.getId())
                                .classGroupId(schedule.getClassGroup().getId())
                                .classGroupName(schedule.getClassGroup().getName())
                                .teacherId(schedule.getTeacher().getId())
                                .teacherName(schedule.getTeacher().getUser().getFullName())
                                .subjectId(schedule.getSubject().getId())
                                .subjectName(schedule.getSubject().getName())
                                .subjectShortName(schedule.getSubject().getShortName())
                                .dayOfWeek(schedule.getDayOfWeek())
                                .startTime(schedule.getStartTime())
                                .endTime(schedule.getEndTime())
                                .room(schedule.getClassroom() != null ? (schedule.getClassroom().getName() != null
                                                ? schedule.getClassroom().getName()
                                                : schedule.getClassroom().getRoomNumber()) : null)
                                .lessonNumber(schedule.getLessonNumber())
                                .build();
        }
}
