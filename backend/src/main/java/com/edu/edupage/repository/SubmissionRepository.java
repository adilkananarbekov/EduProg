package com.edu.edupage.repository;

import com.edu.edupage.entity.Submission;
import com.edu.edupage.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByHomeworkId(Long homeworkId);
    
    List<Submission> findByStudentId(Long studentId);
    
    Optional<Submission> findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
    
    List<Submission> findByHomeworkIdAndStatus(Long homeworkId, SubmissionStatus status);
    
    long countByHomeworkId(Long homeworkId);
    
    long countByHomeworkIdAndStatus(Long homeworkId, SubmissionStatus status);
    
    boolean existsByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
}
