package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSimpleDTO {
    private String id;
    private String name;
    private String title;
    private String avatarUrl;
}
