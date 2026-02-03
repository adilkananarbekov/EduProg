package com.edu.edupage.controller;

import com.edu.edupage.dto.CreateEventRequest;
import com.edu.edupage.dto.EventDTO;
import com.edu.edupage.entity.EventType;
import com.edu.edupage.entity.Role;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getMyEvents(@AuthenticationPrincipal User user) {
        List<EventDTO> events;
        switch (user.getRole()) {
            case STUDENT -> {
                var student = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Student profile not found"));
                events = eventService.getUpcomingEventsForStudent(student.getId());
            }
            case TEACHER -> events = eventService.getUpcomingEventsForTeacher();
            case ADMIN -> events = eventService.getAllUpcomingEvents();
            default -> throw new IllegalStateException("Unknown role");
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllUpcomingEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/range")
    public ResponseEntity<List<EventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(eventService.getEventsByDateRange(start, end));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EventDTO>> getEventsByType(@PathVariable EventType type) {
        return ResponseEntity.ok(eventService.getEventsByType(type));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(eventService.createEvent(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
