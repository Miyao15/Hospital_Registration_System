package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSearchDTO {
    private String keyword;
    private String departmentId;
    private String title;
    private String medicalItemId; // 检查项目ID，用于筛选关联科室的医生
    private String region;       // 地区筛选
    private String city;          // 城市筛选
    private String province;      // 省份筛选
    private String district;      // 区县筛选
    private Integer page = 0;
    private Integer size = 10;
}
