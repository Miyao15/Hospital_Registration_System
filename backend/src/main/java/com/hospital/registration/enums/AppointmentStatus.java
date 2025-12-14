package com.hospital.registration.enums;

public enum AppointmentStatus {
    PENDING("待就诊"),
    CHECKED_IN("已到诊"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    EXPIRED("已过期");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
