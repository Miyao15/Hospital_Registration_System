package com.hospital.registration.dto;

import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private String id;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private String realName; // 从Patient/Doctor/Admin表中获取的姓名
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Integer loginFailures;
    private LocalDateTime lockedUntil;
}

