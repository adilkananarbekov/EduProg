package com.edu.edupage.controller;

import com.edu.edupage.dto.CreateExamRequest;
import com.edu.edupage.dto.ExamDTO;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.repository.TeacherRepository;
import com.edu.edupage.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping
    public ResponseEntity<List<ExamDTO>> getMyExams(@AuthenticationPrincipal User user) {
        switch (user.getRole()) {
            case STUDENT -> {
                var student = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Student profile not found"));
                return ResponseEntity.ok(examService.getUpcomingExamsForClass(student.getClassGroup().getId()));
            }
            case TEACHER -> {
                var teacher = teacherRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
                return ResponseEntity.ok(examService.getExamsByTeacher(teacher.getId()));
            }
            default -> throw new IllegalStateException("Admins should use /api/exams/all");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        return ResponseEntity.ok(examService.getUpcomingExamsForClass(null));
    }

    @GetMapping("/class/{classGroupId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<ExamDTO>> getExamsByClass(@PathVariable Long classGroupId) {
        return ResponseEntity.ok(examService.getExamsForClass(classGroupId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> getExam(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ExamDTO> createExam(
            @Valid @RequestBody CreateExamRequest request,
            @AuthenticationPrincipal User user) {
        var teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
        return ResponseEntity.ok(examService.createExam(request, teacher.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ExamDTO> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody CreateExamRequest request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
