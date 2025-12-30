package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private String id;
    private String name;
    private String category;
    private String description;
    private String location;
    private String phone;
    private DoctorSimpleDTO director;
    private Integer doctorCount;
    private Boolean enabled;
    private Integer sortOrder;
}
