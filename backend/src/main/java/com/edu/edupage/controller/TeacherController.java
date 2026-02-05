package com.edu.edupage.controller;

import com.edu.edupage.dto.ScheduleDTO;
import com.edu.edupage.dto.TeacherDTO;
import com.edu.edupage.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Teacher information and schedules")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @Operation(summary = "Get all teachers", description = "Returns a list of all teachers with their subjects")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID", description = "Returns detailed information about a specific teacher")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping("/{id}/schedule")
    @Operation(summary = "Get teacher's schedule", description = "Returns the weekly schedule for a specific teacher")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherSchedule(id));
    }
}
