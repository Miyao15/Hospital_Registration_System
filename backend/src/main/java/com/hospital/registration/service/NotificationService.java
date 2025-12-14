package com.hospital.registration.service;

import com.hospital.registration.dto.NotificationDTO;
import com.hospital.registration.entity.Notification;
import com.hospital.registration.enums.NotificationType;
import com.hospital.registration.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public void sendNotification(String userId, String title, String content, 
                                  NotificationType type, String relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
    
    public Page<NotificationDTO> getNotifications(String userId, Boolean unreadOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications;
        
        if (unreadOnly != null && unreadOnly) {
            notifications = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(
                    userId, false, pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return notifications.map(this::convertToDTO);
    }
    
    public int getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    @Transactional
    public void markAsRead(String notificationId, String userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }
    
    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
    }
    
    @Transactional
    public void deleteNotification(String notificationId, String userId) {
        notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .ifPresent(notificationRepository::delete);
    }
    
    // 预约成功通知
    public void sendAppointmentSuccessNotification(String userId, String appointmentNo, 
                                                    String doctorName, String date, String time) {
        String title = "预约成功";
        String content = String.format("您已成功预约%s医生，就诊时间：%s %s，预约单号：%s。请按时就诊。",
                doctorName, date, time, appointmentNo);
        sendNotification(userId, title, content, NotificationType.APPOINTMENT, appointmentNo);
    }
    
    // 就诊提醒通知
    public void sendReminderNotification(String userId, String doctorName, String date, String time) {
        String title = "就诊提醒";
        String content = String.format("温馨提醒：您预约的%s医生就诊时间为%s %s，请提前到达医院。",
                doctorName, date, time);
        sendNotification(userId, title, content, NotificationType.REMINDER, null);
    }
    
    // 预约取消通知
    public void sendCancelNotification(String userId, String reason) {
        String title = "预约已取消";
        String content = "您的预约已取消。" + (reason != null ? "原因：" + reason : "");
        sendNotification(userId, title, content, NotificationType.CANCEL, null);
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setType(notification.getType().name());
        dto.setTypeName(notification.getType().getDisplayName());
        dto.setRelatedId(notification.getRelatedId());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
