package com.edu.edupage.service;

import com.edu.edupage.dto.CreateEventRequest;
import com.edu.edupage.dto.EventDTO;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.ClassGroupRepository;
import com.edu.edupage.repository.EventRepository;
import com.edu.edupage.repository.StudentRepository;
import com.edu.edupage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final StudentRepository studentRepository;

    public List<EventDTO> getUpcomingEventsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return eventRepository.findUpcomingForStudent(LocalDate.now(), Role.STUDENT, student.getClassGroup().getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEventsForTeacher() {
        return eventRepository.findUpcomingForTeacher(LocalDate.now(), Role.TEACHER)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getAllUpcomingEvents() {
        return eventRepository.findAllUpcoming(LocalDate.now())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByDateRange(LocalDate start, LocalDate end) {
        return eventRepository.findByDateRange(start, end)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByType(EventType type) {
        return eventRepository.findByType(type)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapToDTO(event);
    }

    public EventDTO createEvent(CreateEventRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ClassGroup targetClassGroup = null;
        if (request.getTargetClassGroupId() != null) {
            targetClassGroup = classGroupRepository.findById(request.getTargetClassGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class group not found"));
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .type(request.getType())
                .createdBy(user)
                .targetRole(request.getTargetRole())
                .targetClassGroup(targetClassGroup)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        return mapToDTO(eventRepository.save(event));
    }

    public EventDTO updateEvent(Long id, CreateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setType(request.getType());
        event.setTargetRole(request.getTargetRole());
        event.setIsPublic(request.getIsPublic());

        if (request.getTargetClassGroupId() != null) {
            ClassGroup classGroup = classGroupRepository.findById(request.getTargetClassGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class group not found"));
            event.setTargetClassGroup(classGroup);
        } else {
            event.setTargetClassGroup(null);
        }

        return mapToDTO(eventRepository.save(event));
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .type(event.getType())
                .createdByName(event.getCreatedBy().getFirstName() + " " + event.getCreatedBy().getLastName())
                .targetRole(event.getTargetRole())
                .targetClassGroupId(event.getTargetClassGroup() != null ? event.getTargetClassGroup().getId() : null)
                .targetClassGroupName(event.getTargetClassGroup() != null ? event.getTargetClassGroup().getName() : null)
                .isPublic(event.getIsPublic())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
