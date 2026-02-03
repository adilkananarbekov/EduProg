package com.edu.edupage.repository;

import com.edu.edupage.entity.BookLoan;
import com.edu.edupage.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    List<BookLoan> findByUserId(Long userId);
    
    List<BookLoan> findByUserIdAndStatus(Long userId, LoanStatus status);
    
    List<BookLoan> findByBookId(Long bookId);
    
    @Query("SELECT l FROM BookLoan l WHERE l.user.id = :userId AND l.status = 'BORROWED'")
    List<BookLoan> findActiveLoansByUser(@Param("userId") Long userId);
    
    @Query("SELECT l FROM BookLoan l WHERE l.dueDate < :today AND l.status = 'BORROWED'")
    List<BookLoan> findOverdueLoans(@Param("today") LocalDate today);
    
    @Query("SELECT COUNT(l) FROM BookLoan l WHERE l.user.id = :userId AND l.status = 'BORROWED'")
    long countActiveLoans(@Param("userId") Long userId);
    
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);
}
