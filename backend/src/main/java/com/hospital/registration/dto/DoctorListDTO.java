package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorListDTO {
    private String id;
    private String name;
    private String title;
    private String employeeId;         // 工号
    private String licenseNumber;      // 执业证号
    private String departmentId;
    private String departmentName;
    private String avatarUrl;
    private String specialty;
    private String scheduleInfo;
    private String onlineStatus;  // AVAILABLE, FULL, REST
    private String status;        // 医生状态 (PENDING, ACTIVE, LOCKED)
    private String phone;         // 手机号
    private Double rating;        // 平均评分
    private Integer reviewCount;  // 评价数量
    
    // 医院信息
    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private Double hospitalLongitude;
    private Double hospitalLatitude;
}
