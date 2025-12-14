package com.hospital.registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleDTO {
    
    @NotBlank(message = "医生ID不能为空")
    private String doctorId;
    
    @NotNull(message = "排班日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    private List<String> periods;  // MORNING, AFTERNOON, EVENING
    
    private Integer morningSlots = 20;
    private Integer afternoonSlots = 15;
    private Integer eveningSlots = 10;
}
