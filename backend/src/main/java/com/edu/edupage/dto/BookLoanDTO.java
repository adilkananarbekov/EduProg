package com.edu.edupage.dto;

import com.edu.edupage.entity.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoanDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Long userId;
    private String userName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private Integer renewCount;
    private boolean isOverdue;
    private LocalDateTime createdAt;
}
