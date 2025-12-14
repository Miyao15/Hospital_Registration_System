package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.NotificationDTO;
import com.hospital.registration.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ApiResponse<Page<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = userDetails.getUsername();
        return ApiResponse.success(notificationService.getNotifications(userId, unreadOnly, page, size));
    }
    
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Integer>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ApiResponse.success(Map.of("count", notificationService.getUnreadCount(userId)));
    }
    
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String userId = userDetails.getUsername();
        notificationService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String userId = userDetails.getUsername();
        notificationService.deleteNotification(id, userId);
        return ApiResponse.success(null);
    }
}
