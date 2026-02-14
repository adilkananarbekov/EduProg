package com.edu.edupage.service;

import com.edu.edupage.dto.AnnouncementDTO;
import com.edu.edupage.dto.CreateAnnouncementRequest;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

        private final AnnouncementRepository announcementRepository;
        private final UserRepository userRepository;
        private final ClassGroupRepository classGroupRepository;
        private final StudentRepository studentRepository;
        private final ActivityLogService activityLogService;

        public List<AnnouncementDTO> getAnnouncementsForStudent(Long studentId) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

                Long classGroupId = student.getClassGroup() != null ? student.getClassGroup().getId() : null;

                return announcementRepository.findActiveAnnouncementsForStudent(
                                Role.STUDENT, classGroupId, LocalDateTime.now())
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<AnnouncementDTO> getAnnouncementsForTeacher() {
                return announcementRepository.findActiveAnnouncementsForRole(Role.TEACHER, LocalDateTime.now())
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<AnnouncementDTO> getAllAnnouncements() {
                return announcementRepository.findAllOrderByImportanceAndDate()
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public AnnouncementDTO createAnnouncement(CreateAnnouncementRequest request, Long authorId) {
                User author = userRepository.findById(authorId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

                Announcement announcement = Announcement.builder()
                                .title(request.getTitle())
                                .content(request.getContent())
                                .author(author)
                                .targetRole(request.getTargetRole())
                                .important(request.getImportant() != null ? request.getImportant() : false)
                                .expiresAt(request.getExpiresAt())
                                .build();

                if (request.getTargetClassGroupId() != null) {
                        ClassGroup classGroup = classGroupRepository.findById(request.getTargetClassGroupId())
                                        .orElseThrow(
                                                        () -> new ResourceNotFoundException("ClassGroup", "id",
                                                                        request.getTargetClassGroupId()));
                        announcement.setTargetClassGroup(classGroup);
                }

                announcement = announcementRepository.save(announcement);

                activityLogService.logActivity(
                                "ANNOUNCEMENT_POSTED",
                                "New announcement posted: " + announcement.getTitle(),
                                author.getId());

                return mapToDTO(announcement);
        }

        @Transactional
        public void deleteAnnouncement(Long id) {
                if (!announcementRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Announcement", "id", id);
                }
                announcementRepository.deleteById(id);
        }

        private AnnouncementDTO mapToDTO(Announcement announcement) {
                return AnnouncementDTO.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .authorId(announcement.getAuthor().getId())
                                .authorName(announcement.getAuthor().getFullName())
                                .targetRole(announcement.getTargetRole())
                                .targetClassGroupId(
                                                announcement.getTargetClassGroup() != null
                                                                ? announcement.getTargetClassGroup().getId()
                                                                : null)
                                .targetClassGroupName(
                                                announcement.getTargetClassGroup() != null
                                                                ? announcement.getTargetClassGroup().getName()
                                                                : null)
                                .important(announcement.getImportant())
                                .publishedAt(announcement.getPublishedAt())
                                .expiresAt(announcement.getExpiresAt())
                                .createdAt(announcement.getCreatedAt())
                                .build();
        }
}
