package com.edu.edupage.repository;

import com.edu.edupage.entity.Event;
import com.edu.edupage.entity.EventType;
import com.edu.edupage.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today AND (e.targetRole IS NULL OR e.targetRole = :role) AND (e.targetClassGroup IS NULL OR e.targetClassGroup.id = :classGroupId) ORDER BY e.eventDate ASC")
    List<Event> findUpcomingForStudent(@Param("today") LocalDate today, @Param("role") Role role, @Param("classGroupId") Long classGroupId);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today AND (e.targetRole IS NULL OR e.targetRole = :role) ORDER BY e.eventDate ASC")
    List<Event> findUpcomingForTeacher(@Param("today") LocalDate today, @Param("role") Role role);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today ORDER BY e.eventDate ASC")
    List<Event> findAllUpcoming(@Param("today") LocalDate today);
    
    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);
    
    List<Event> findByType(EventType type);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :start AND :end ORDER BY e.eventDate ASC")
    List<Event> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    List<Event> findByCreatedById(Long userId);
}
