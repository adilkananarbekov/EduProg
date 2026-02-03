package com.edu.edupage.controller;

import com.edu.edupage.dto.StudentDashboardDTO;
import com.edu.edupage.dto.TeacherDashboardDTO;
import com.edu.edupage.entity.User;
import com.edu.edupage.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDashboardDTO> getStudentDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(user.getId()));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<TeacherDashboardDTO> getTeacherDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(user.getId()));
    }
}
