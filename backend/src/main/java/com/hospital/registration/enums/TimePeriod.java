package com.hospital.registration.enums;

public enum TimePeriod {
    MORNING("上午"),
    AFTERNOON("下午"),
    EVENING("晚上");
    
    private final String displayName;
    
    TimePeriod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
