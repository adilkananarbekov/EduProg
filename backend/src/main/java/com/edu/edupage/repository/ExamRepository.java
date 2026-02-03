package com.edu.edupage.repository;

import com.edu.edupage.entity.Exam;
import com.edu.edupage.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByClassGroupId(Long classGroupId);
    
    List<Exam> findByTeacherId(Long teacherId);
    
    List<Exam> findBySubjectId(Long subjectId);
    
    @Query("SELECT e FROM Exam e WHERE e.classGroup.id = :classGroupId AND e.examDate >= :today ORDER BY e.examDate ASC")
    List<Exam> findUpcomingByClassGroup(@Param("classGroupId") Long classGroupId, @Param("today") LocalDate today);
    
    @Query("SELECT e FROM Exam e WHERE e.teacher.id = :teacherId AND e.examDate >= :today ORDER BY e.examDate ASC")
    List<Exam> findUpcomingByTeacher(@Param("teacherId") Long teacherId, @Param("today") LocalDate today);
    
    List<Exam> findByExamDateBetween(LocalDate start, LocalDate end);
    
    List<Exam> findByType(ExamType type);
}
