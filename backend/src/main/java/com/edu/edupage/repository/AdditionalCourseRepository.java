package com.edu.edupage.repository;

import com.edu.edupage.entity.AdditionalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalCourseRepository extends JpaRepository<AdditionalCourse, Long> {

    Optional<AdditionalCourse> findByName(String name);

    List<AdditionalCourse> findAllByOrderByNameAsc();
}
