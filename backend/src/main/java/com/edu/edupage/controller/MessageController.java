package com.edu.edupage.controller;

import com.edu.edupage.dto.MessageDTO;
import com.edu.edupage.dto.SendMessageRequest;
import com.edu.edupage.entity.User;
import com.edu.edupage.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/inbox")
    public ResponseEntity<List<MessageDTO>> getInbox(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getInbox(user.getId()));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageDTO>> getSent(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getSent(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<MessageDTO>> getUnread(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getUnread(user.getId()));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("count", messageService.getUnreadCount(user.getId())));
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getConversation(user.getId(), userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getMessage(id, user.getId()));
    }

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.sendMessage(request, user.getId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        messageService.markAsRead(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        messageService.deleteMessage(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
