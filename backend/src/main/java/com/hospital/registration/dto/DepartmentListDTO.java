package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentListDTO {
    private String category;
    private List<DepartmentDTO> departments;
}
