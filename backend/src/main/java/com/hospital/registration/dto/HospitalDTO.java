package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    private String id;
    private String name;
    private String address;
    private String phone;
    private Double longitude;
    private Double latitude;
    private String description;
}
