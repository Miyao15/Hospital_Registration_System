package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String id;
    private String title;
    private String content;
    private String type;
    private String typeName;
    private String relatedId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
