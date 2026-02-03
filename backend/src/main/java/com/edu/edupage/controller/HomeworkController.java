package com.edu.edupage.controller;

import com.edu.edupage.dto.*;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.repository.TeacherRepository;
import com.edu.edupage.service.HomeworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    // Student endpoints
    @GetMapping
    public ResponseEntity<List<HomeworkDTO>> getMyHomework(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(homeworkService.getHomeworkForClass(student.getClassGroup().getId(), student.getId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<HomeworkDTO>> getMyPendingHomework(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(homeworkService.getPendingHomeworkForClass(student.getClassGroup().getId(), student.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeworkDTO> getHomework(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long studentId = null;
        var studentOpt = studentRepository.findByUserId(user.getId());
        if (studentOpt.isPresent()) {
            studentId = studentOpt.get().getId();
        }
        return ResponseEntity.ok(homeworkService.getHomeworkById(id, studentId));
    }

    // Teacher endpoints
    @GetMapping("/teacher")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDTO>> getTeacherHomework(@AuthenticationPrincipal User user) {
        var teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
        return ResponseEntity.ok(homeworkService.getHomeworkByTeacher(teacher.getId()));
    }

    @GetMapping("/class/{classGroupId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDTO>> getClassHomework(@PathVariable Long classGroupId) {
        return ResponseEntity.ok(homeworkService.getHomeworkForClass(classGroupId, null));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDTO> createHomework(
            @Valid @RequestBody CreateHomeworkRequest request,
            @AuthenticationPrincipal User user) {
        var teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
        return ResponseEntity.ok(homeworkService.createHomework(request, teacher.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDTO> updateHomework(
            @PathVariable Long id,
            @Valid @RequestBody CreateHomeworkRequest request) {
        return ResponseEntity.ok(homeworkService.updateHomework(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }

    // Submission endpoints
    @PostMapping("/submit")
    public ResponseEntity<SubmissionDTO> submitHomework(
            @Valid @RequestBody SubmitHomeworkRequest request,
            @AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(homeworkService.submitHomework(request, student.getId()));
    }

    @GetMapping("/{homeworkId}/submissions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<SubmissionDTO>> getSubmissions(@PathVariable Long homeworkId) {
        return ResponseEntity.ok(homeworkService.getSubmissionsForHomework(homeworkId));
    }

    @GetMapping("/my-submissions")
    public ResponseEntity<List<SubmissionDTO>> getMySubmissions(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(homeworkService.getSubmissionsByStudent(student.getId()));
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal User user) {
        var teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
        return ResponseEntity.ok(homeworkService.gradeSubmission(submissionId, request, teacher.getId()));
    }
}
