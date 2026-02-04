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

    public List<ScheduleDTO> getWeeklyScheduleForClass(Long classGroupId) {
        return scheduleRepository.findWeeklyScheduleByClassGroup(classGroupId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getDailyScheduleForClass(Long classGroupId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findDailyScheduleByClassGroup(classGroupId, dayOfWeek)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getDailyScheduleForTeacher(Long teacherId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findDailyScheduleByTeacher(teacherId, dayOfWeek)
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

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDTO createSchedule(CreateScheduleRequest request) {
        // Validate no conflicts
        validateNoConflicts(request.getTeacherId(), request.getClassGroupId(),
                request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id", request.getClassGroupId()));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Schedule schedule = Schedule.builder()
                .classGroup(classGroup)
                .teacher(teacher)
                .subject(subject)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(request.getRoom())
                .lessonNumber(request.getLessonNumber())
                .build();

        schedule = scheduleRepository.save(schedule);
        return mapToDTO(schedule);
    }

    @Transactional
    public List<ScheduleDTO> generateSchedule(GenerateScheduleRequest request) {
        List<Schedule> generatedSchedules = new ArrayList<>();

        LocalTime dayStart = request.getDayStartTime() != null ? request.getDayStartTime() : LocalTime.of(8, 0);
        LocalTime dayEnd = request.getDayEndTime() != null ? request.getDayEndTime() : LocalTime.of(15, 0);
        int lessonDuration = request.getLessonDurationMinutes() != null ? request.getLessonDurationMinutes() : 45;
        int breakDuration = request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 15;

        DayOfWeek[] weekDays = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

        for (GenerateScheduleRequest.TeacherSubjectMapping mapping : request.getTeacherSubjectMappings()) {
            Teacher teacher = teacherRepository.findById(mapping.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", mapping.getTeacherId()));

            Subject subject = subjectRepository.findById(mapping.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", mapping.getSubjectId()));

            for (Long classGroupId : mapping.getClassGroupIds()) {
                ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                        .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id", classGroupId));

                int lessonsNeeded = subject.getHoursPerWeek();
                int lessonsScheduled = 0;

                outerLoop: for (DayOfWeek day : weekDays) {
                    LocalTime currentTime = dayStart;
                    int lessonNumber = 1;

                    while (currentTime.plusMinutes(lessonDuration).isBefore(dayEnd) ||
                            currentTime.plusMinutes(lessonDuration).equals(dayEnd)) {

                        LocalTime endTime = currentTime.plusMinutes(lessonDuration);

                        // Check for conflicts
                        List<Schedule> teacherConflicts = scheduleRepository.findConflictingTeacherSchedules(
                                teacher.getId(), day, currentTime, endTime);
                        List<Schedule> classConflicts = scheduleRepository.findConflictingClassSchedules(
                                classGroup.getId(), day, currentTime, endTime);

                        if (teacherConflicts.isEmpty() && classConflicts.isEmpty()) {
                            Schedule schedule = Schedule.builder()
                                    .classGroup(classGroup)
                                    .teacher(teacher)
                                    .subject(subject)
                                    .dayOfWeek(day)
                                    .startTime(currentTime)
                                    .endTime(endTime)
                                    .lessonNumber(lessonNumber)
                                    .build();

                            schedule = scheduleRepository.save(schedule);
                            generatedSchedules.add(schedule);
                            lessonsScheduled++;

                            if (lessonsScheduled >= lessonsNeeded) {
                                break outerLoop;
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
            LocalTime startTime, LocalTime endTime, Long excludeId) {
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
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .room(schedule.getRoom())
                .lessonNumber(schedule.getLessonNumber())
                .build();
    }
}
