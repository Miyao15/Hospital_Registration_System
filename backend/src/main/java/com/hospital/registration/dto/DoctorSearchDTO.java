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
    private Integer page = 0;
    private Integer size = 10;
}
