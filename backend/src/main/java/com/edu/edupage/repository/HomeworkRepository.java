package com.edu.edupage.repository;

import com.edu.edupage.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    
    List<Homework> findByClassGroupId(Long classGroupId);
    
    List<Homework> findByTeacherId(Long teacherId);
    
    List<Homework> findBySubjectId(Long subjectId);
    
    @Query("SELECT h FROM Homework h WHERE h.classGroup.id = :classGroupId AND h.dueDate >= :today ORDER BY h.dueDate ASC")
    List<Homework> findPendingByClassGroup(@Param("classGroupId") Long classGroupId, @Param("today") LocalDate today);
    
    @Query("SELECT h FROM Homework h WHERE h.classGroup.id = :classGroupId AND h.dueDate < :today ORDER BY h.dueDate DESC")
    List<Homework> findPastByClassGroup(@Param("classGroupId") Long classGroupId, @Param("today") LocalDate today);
    
    List<Homework> findByClassGroupIdAndSubjectId(Long classGroupId, Long subjectId);
    
    @Query("SELECT h FROM Homework h WHERE h.teacher.id = :teacherId AND h.dueDate >= :today ORDER BY h.dueDate ASC")
    List<Homework> findPendingByTeacher(@Param("teacherId") Long teacherId, @Param("today") LocalDate today);
}
