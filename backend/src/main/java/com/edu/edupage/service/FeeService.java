package com.edu.edupage.service;

import com.edu.edupage.dto.FeeDTO;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.FeeRepository;
import com.edu.edupage.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeService {

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public List<FeeDTO> getStudentFees(Long studentId) {
        return feeRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FeeDTO> getStudentPendingFees(Long studentId) {
        return feeRepository.findUnpaidByStudent(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalPendingAmount(Long studentId) {
        BigDecimal total = feeRepository.getTotalPendingAmount(studentId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<FeeDTO> getAllPendingFees() {
        return feeRepository.findByStatus(FeeStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FeeDTO> getOverdueFees() {
        return feeRepository.findOverdueFees(LocalDate.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FeeDTO createFee(Long studentId, String description, FeeType type, 
            BigDecimal amount, LocalDate dueDate, String academicYear, String semester) {
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Fee fee = Fee.builder()
                .student(student)
                .description(description)
                .type(type)
                .amount(amount)
                .dueDate(dueDate)
                .academicYear(academicYear)
                .semester(semester)
                .build();

        fee = feeRepository.save(fee);

        // Notify student
        notificationService.createNotification(
                student.getUser().getId(),
                "New Fee: " + description,
                "Amount: $" + amount + " - Due: " + dueDate,
                NotificationType.INFO,
                "FEE",
                fee.getId()
        );

        return mapToDTO(fee);
    }

    public FeeDTO makePayment(Long feeId, BigDecimal paymentAmount) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee not found"));

        if (fee.getStatus() == FeeStatus.PAID) {
            throw new IllegalStateException("Fee already fully paid");
        }

        BigDecimal newPaidAmount = fee.getPaidAmount().add(paymentAmount);
        fee.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(fee.getAmount()) >= 0) {
            fee.setStatus(FeeStatus.PAID);
            fee.setPaidDate(LocalDate.now());
        } else {
            fee.setStatus(FeeStatus.PARTIAL);
        }

        fee = feeRepository.save(fee);

        // Notify student
        notificationService.createNotification(
                fee.getStudent().getUser().getId(),
                "Payment Received",
                "Amount: $" + paymentAmount + " for " + fee.getDescription(),
                NotificationType.INFO,
                "FEE",
                fee.getId()
        );

        return mapToDTO(fee);
    }

    public FeeDTO waiveFee(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee not found"));

        fee.setStatus(FeeStatus.WAIVED);
        return mapToDTO(feeRepository.save(fee));
    }

    public void markOverdueFees() {
        List<Fee> overdue = feeRepository.findOverdueFees(LocalDate.now());
        for (Fee fee : overdue) {
            fee.setStatus(FeeStatus.OVERDUE);
            feeRepository.save(fee);
        }
    }

    private FeeDTO mapToDTO(Fee fee) {
        return FeeDTO.builder()
                .id(fee.getId())
                .studentId(fee.getStudent().getId())
                .studentName(fee.getStudent().getUser().getFirstName() + " " + fee.getStudent().getUser().getLastName())
                .description(fee.getDescription())
                .type(fee.getType())
                .amount(fee.getAmount())
                .paidAmount(fee.getPaidAmount())
                .remainingAmount(fee.getAmount().subtract(fee.getPaidAmount()))
                .dueDate(fee.getDueDate())
                .paidDate(fee.getPaidDate())
                .status(fee.getStatus())
                .academicYear(fee.getAcademicYear())
                .semester(fee.getSemester())
                .isOverdue(fee.getDueDate().isBefore(LocalDate.now()) && 
                        (fee.getStatus() == FeeStatus.PENDING || fee.getStatus() == FeeStatus.PARTIAL))
                .createdAt(fee.getCreatedAt())
                .build();
    }
}
