package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.NotificationDTO;
import com.hospital.registration.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @GetMapping
    public ApiResponse<Page<NotificationDTO>> getNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = getCurrentUserId();
        log.info("获取通知列表 - userId: {}, type: {}, page: {}, size: {}", userId, type, page, size);
        return ApiResponse.success(notificationService.getNotifications(userId, unreadOnly, type, page, size));
    }
    
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Integer>> getUnreadCount() {
        String userId = getCurrentUserId();
        return ApiResponse.success(Map.of("count", notificationService.getUnreadCount(userId)));
    }
    
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable String id) {
        String userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        String userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable String id) {
        String userId = getCurrentUserId();
        notificationService.deleteNotification(id, userId);
        return ApiResponse.success(null);
    }
}
