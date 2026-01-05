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
    private String province;  // 省份
    private String city;      // 城市
    private String district;  // 区县
    private String postalCode; // 邮编
    private String region;    // 地区
    private String phone;
    private Double longitude;
    private Double latitude;
    private String description;
    private Boolean enabled;
}
