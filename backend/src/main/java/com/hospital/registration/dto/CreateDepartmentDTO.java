package com.hospital.registration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentDTO {
    
    @NotBlank(message = "科室名称不能为空")
    private String name;
    
    @NotBlank(message = "科室类别不能为空")
    private String category;
    
    private String description;
    private String location;
    private String phone;
    private String directorId;
    private Integer sortOrder;
}
