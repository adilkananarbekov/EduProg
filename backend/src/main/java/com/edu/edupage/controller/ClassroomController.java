package com.edu.edupage.controller;

import com.edu.edupage.entity.Classroom;
import com.edu.edupage.repository.ClassroomRepository;
import com.edu.edupage.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomRepository classroomRepository;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomRepository.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long id, @RequestBody Classroom classroomDetails) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));

        if (classroomDetails.getName() != null) {
            classroom.setName(classroomDetails.getName());
        }
        if (classroomDetails.getCapacity() != null) {
            classroom.setCapacity(classroomDetails.getCapacity());
        }
        // Room number and floor should generally not change or require validaton checks
        // if they do

        return ResponseEntity.ok(classroomRepository.save(classroom));
    }
}
