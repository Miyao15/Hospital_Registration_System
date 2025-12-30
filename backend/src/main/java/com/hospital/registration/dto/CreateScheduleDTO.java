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
    
    private List<Integer> weekdays;  // 0=周日, 1=周一, ..., 6=周六
    
    private List<TimeSlotConfig> timeSlots;  // 时间段配置
    
    // 兼容旧版本
    private List<String> periods;  // MORNING, AFTERNOON, EVENING
    private Integer morningSlots = 20;
    private Integer afternoonSlots = 15;
    private Integer eveningSlots = 10;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotConfig {
        private String period;  // MORNING, AFTERNOON, EVENING
        private String startTime;  // HH:mm:ss
        private String endTime;   // HH:mm:ss
        private Integer totalSlots;
    }
}
