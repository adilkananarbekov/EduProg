package com.edu.edupage.dto;

import com.edu.edupage.entity.FeeStatus;
import com.edu.edupage.entity.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String description;
    private FeeType type;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private FeeStatus status;
    private String academicYear;
    private String semester;
    private boolean isOverdue;
    private LocalDateTime createdAt;
}
