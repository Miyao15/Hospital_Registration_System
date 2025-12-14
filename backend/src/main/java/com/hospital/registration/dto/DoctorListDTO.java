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
    private String departmentId;
    private String departmentName;
    private String avatarUrl;
    private String specialty;
    private String scheduleInfo;
    private String onlineStatus;  // AVAILABLE, FULL, REST
}
