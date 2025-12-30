package com.hospital.registration.dto;

import lombok.Data;

@Data
public class UpdateDoctorProfileDTO {
    private String name;
    private String title;
    private String licenseNumber;
    private String specialty;
    private String introduction;
    private String education;
    private String avatarUrl;
}

