package com.edu.edupage.repository;

import com.edu.edupage.entity.Student;
import com.edu.edupage.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);

    List<Student> findByClassGroup(ClassGroup classGroup);

    List<Student> findByClassGroupId(Long classGroupId);
    
    int countByClassGroupId(Long classGroupId);
    
    List<Student> findByClassGroupIsNull();
}
