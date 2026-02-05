package com.edu.edupage.controller;

import com.edu.edupage.dto.CreateScheduleRequest;
import com.edu.edupage.dto.GenerateScheduleRequest;
import com.edu.edupage.dto.ScheduleDTO;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.repository.TeacherRepository;
import com.edu.edupage.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/week")
    public ResponseEntity<List<ScheduleDTO>> getMyWeeklySchedule(@AuthenticationPrincipal User user) {
        List<ScheduleDTO> schedule;

        switch (user.getRole()) {
            case STUDENT -> {
                var student = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Student profile not found"));
                schedule = scheduleService.getStudentSchedule(student);
            }
            case TEACHER -> {
                var teacher = teacherRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
                schedule = scheduleService.getWeeklyScheduleForTeacher(teacher.getId());
            }
            case ADMIN, OPERATOR -> schedule = scheduleService.getAllSchedules();
            default -> throw new IllegalStateException("Unknown role");
        }

        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/class/{classGroupId}")
    public ResponseEntity<List<ScheduleDTO>> getClassSchedule(@PathVariable Long classGroupId) {
        return ResponseEntity.ok(scheduleService.getWeeklyScheduleForClass(classGroupId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedule(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getWeeklyScheduleForTeacher(teacherId));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<ScheduleDTO>> getClassroomSchedule(@PathVariable Long classroomId) {
        return ResponseEntity.ok(scheduleService.getWeeklyScheduleForClassroom(classroomId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'OPERATOR')")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<ScheduleDTO>> generateSchedule(@Valid @RequestBody GenerateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.generateSchedule(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'OPERATOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
