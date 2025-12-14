package com.hospital.registration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RescheduleAppointmentDTO {
    
    @NotBlank(message = "新时间段ID不能为空")
    private String newTimeSlotId;
    
    private String reason;
}
