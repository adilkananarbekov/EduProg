package com.edu.edupage.service;

import com.edu.edupage.dto.MessageDTO;
import com.edu.edupage.dto.SendMessageRequest;
import com.edu.edupage.entity.Message;
import com.edu.edupage.entity.User;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.MessageRepository;
import com.edu.edupage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<MessageDTO> getInbox(Long userId) {
        return messageRepository.findInbox(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getSent(Long userId) {
        return messageRepository.findSent(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getUnread(Long userId) {
        return messageRepository.findUnread(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return messageRepository.countUnread(userId);
    }

    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Check if user is sender or recipient
        if (!message.getSender().getId().equals(userId) && !message.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have access to this message");
        }

        // Mark as read if recipient is viewing
        if (message.getRecipient().getId().equals(userId) && !message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }

        return mapToDTO(message);
    }

    public MessageDTO sendMessage(SendMessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .subject(request.getSubject())
                .content(request.getContent())
                .build();

        message = messageRepository.save(message);

        // Notify recipient
        notificationService.createNotification(
                recipient.getId(),
                "New message from " + sender.getFirstName() + " " + sender.getLastName(),
                request.getSubject(),
                com.edu.edupage.entity.NotificationType.MESSAGE,
                "MESSAGE",
                message.getId()
        );

        return mapToDTO(message);
    }

    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (message.getSender().getId().equals(userId)) {
            message.setSenderDeleted(true);
        } else if (message.getRecipient().getId().equals(userId)) {
            message.setRecipientDeleted(true);
        } else {
            throw new IllegalArgumentException("You don't have access to this message");
        }

        // If both have deleted, remove from database
        if (message.getSenderDeleted() && message.getRecipientDeleted()) {
            messageRepository.delete(message);
        } else {
            messageRepository.save(message);
        }
    }

    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only mark your own messages as read");
        }

        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .senderEmail(message.getSender().getEmail())
                .recipientId(message.getRecipient().getId())
                .recipientName(message.getRecipient().getFirstName() + " " + message.getRecipient().getLastName())
                .recipientEmail(message.getRecipient().getEmail())
                .subject(message.getSubject())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
