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
    private String title;
    private String departmentId;
    private String departmentName;
    private String avatarUrl;
    private String introduction;
    private String education;
    private String specialty;
    private String scheduleInfo;
    private String onlineStatus;
    private Double avgRating;
    private Integer reviewCount;
}
