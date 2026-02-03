package com.edu.edupage.controller;

import com.edu.edupage.dto.FeeDTO;
import com.edu.edupage.entity.FeeType;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<FeeDTO>> getMyFees(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(feeService.getStudentFees(student.getId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FeeDTO>> getMyPendingFees(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(feeService.getStudentPendingFees(student.getId()));
    }

    @GetMapping("/total-pending")
    public ResponseEntity<Map<String, BigDecimal>> getMyTotalPending(@AuthenticationPrincipal User user) {
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(Map.of("total", feeService.getTotalPendingAmount(student.getId())));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<FeeDTO>> getStudentFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getStudentFees(studentId));
    }

    @GetMapping("/all/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeDTO>> getAllPendingFees() {
        return ResponseEntity.ok(feeService.getAllPendingFees());
    }

    @GetMapping("/all/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeDTO>> getOverdueFees() {
        return ResponseEntity.ok(feeService.getOverdueFees());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDTO> createFee(@RequestBody CreateFeeRequest request) {
        return ResponseEntity.ok(feeService.createFee(
                request.studentId(),
                request.description(),
                request.type(),
                request.amount(),
                request.dueDate(),
                request.academicYear(),
                request.semester()
        ));
    }

    @PostMapping("/{feeId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDTO> makePayment(
            @PathVariable Long feeId,
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(feeService.makePayment(feeId, request.amount()));
    }

    @PostMapping("/{feeId}/waive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDTO> waiveFee(@PathVariable Long feeId) {
        return ResponseEntity.ok(feeService.waiveFee(feeId));
    }

    // Inline request records
    public record CreateFeeRequest(
            Long studentId,
            String description,
            FeeType type,
            BigDecimal amount,
            LocalDate dueDate,
            String academicYear,
            String semester
    ) {}

    public record PaymentRequest(BigDecimal amount) {}
}
