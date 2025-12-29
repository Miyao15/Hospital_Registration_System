package com.hospital.registration.dto.request;

import lombok.Data;

@Data
public class UpdateDoctorProfileRequest {
    private String name;
    private String employeeId;
    private String departmentId;
    private String specialty;
    private String introduction;
    private String avatarUrl;
    private String education;
    private String scheduleInfo;
    // 可以根据需要添加其他可由管理员更新的字段
}
