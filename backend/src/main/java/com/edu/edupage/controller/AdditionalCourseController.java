package com.edu.edupage.controller;

import com.edu.edupage.dto.AdditionalCourseDTO;
import com.edu.edupage.entity.User;
import com.edu.edupage.service.AdditionalCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Additional Courses", description = "Extra-curricular activities management")
public class AdditionalCourseController {

    private final AdditionalCourseService additionalCourseService;

    @GetMapping
    @Operation(summary = "Get all additional courses", description = "Returns all available additional courses")
    public ResponseEntity<List<AdditionalCourseDTO>> getAllCourses() {
        return ResponseEntity.ok(additionalCourseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Returns details of a specific course")
    public ResponseEntity<AdditionalCourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(additionalCourseService.getCourseById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my enrolled courses", description = "Returns courses the current student is enrolled in")
    public ResponseEntity<List<AdditionalCourseDTO>> getMyCourses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(additionalCourseService.getStudentCoursesByUserId(user.getId()));
    }

    @PostMapping("/{courseId}/enroll")
    @Operation(summary = "Enroll in a course", description = "Enrolls the current student in a course")
    public ResponseEntity<Void> enrollInCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        // Get student ID from user
        additionalCourseService.enrollStudent(getStudentIdFromUser(user), courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/enroll")
    @Operation(summary = "Unenroll from a course", description = "Removes the current student from a course")
    public ResponseEntity<Void> unenrollFromCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        additionalCourseService.unenrollStudent(getStudentIdFromUser(user), courseId);
        return ResponseEntity.ok().build();
    }

    private Long getStudentIdFromUser(User user) {
        // This will be retrieved via the student repository in a real implementation
        // For now, we'll rely on the service to handle userId lookup
        return user.getId(); // The service will convert this to student ID
    }
}
