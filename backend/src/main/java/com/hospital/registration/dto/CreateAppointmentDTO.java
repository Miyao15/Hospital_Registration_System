package com.hospital.registration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentDTO {
    
    @NotBlank(message = "医生ID不能为空")
    private String doctorId;
    
    @NotBlank(message = "时间段ID不能为空")
    private String timeSlotId;
    
    @NotBlank(message = "就诊人姓名不能为空")
    private String patientName;
    
    @NotBlank(message = "就诊人电话不能为空")
    private String patientPhone;
    
    private String symptomDesc;
    
    private String medicalItemId;
}
