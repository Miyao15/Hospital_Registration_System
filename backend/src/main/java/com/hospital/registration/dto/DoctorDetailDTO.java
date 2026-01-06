package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDetailDTO {
    private String id;
    private String name;
    private String title;              // 职称显示名称（如"住院医师"）
    private String titleCode;          // 职称枚举值（如"RESIDENT"），用于表单编辑
    private String employeeId;         // 工号
    private String licenseNumber;      // 执业证号
    private String departmentId;
    private String departmentName;
    private String avatarUrl;
    private String avatarData;         // Base64头像数据
    private String introduction;
    private String education;
    private String specialty;
    private String scheduleInfo;
    private String onlineStatus;
    private Double avgRating;          // 平均评分
    private Integer reviewCount;       // 评价数量
    private Double rating;             // rating 映射到 avgRating
    private String phone;               // 手机号码（从User表获取）
}
