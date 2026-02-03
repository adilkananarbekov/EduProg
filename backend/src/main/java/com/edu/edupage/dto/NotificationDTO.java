package com.edu.edupage.dto;

import com.edu.edupage.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
