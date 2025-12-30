package com.hospital.registration.enums;

public enum LeaveStatus {
    PENDING("待审批"),
    APPROVED("已批准"),
    REJECTED("已拒绝"),
    CANCELLED("已撤销");
    
    private final String displayName;
    
    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
