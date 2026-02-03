package com.edu.edupage.repository;

import com.edu.edupage.entity.Fee;
import com.edu.edupage.entity.FeeStatus;
import com.edu.edupage.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentId(Long studentId);
    
    List<Fee> findByStudentIdAndStatus(Long studentId, FeeStatus status);
    
    List<Fee> findByStatus(FeeStatus status);
    
    List<Fee> findByType(FeeType type);
    
    @Query("SELECT f FROM Fee f WHERE f.student.id = :studentId AND f.status IN ('PENDING', 'PARTIAL', 'OVERDUE') ORDER BY f.dueDate ASC")
    List<Fee> findUnpaidByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT f FROM Fee f WHERE f.dueDate < :today AND f.status IN ('PENDING', 'PARTIAL')")
    List<Fee> findOverdueFees(@Param("today") LocalDate today);
    
    @Query("SELECT SUM(f.amount - f.paidAmount) FROM Fee f WHERE f.student.id = :studentId AND f.status IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    BigDecimal getTotalPendingAmount(@Param("studentId") Long studentId);
    
    List<Fee> findByAcademicYear(String academicYear);
}
