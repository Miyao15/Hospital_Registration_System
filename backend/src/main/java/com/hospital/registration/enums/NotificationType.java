package com.hospital.registration.enums;

public enum NotificationType {
    APPOINTMENT("预约通知"),
    REMINDER("就诊提醒"),
    SYSTEM("系统通知"),
    CANCEL("取消通知"),
    APPOINTMENT_REMINDER("就诊提醒"),
    APPOINTMENT_CANCELLED("预约取消");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
