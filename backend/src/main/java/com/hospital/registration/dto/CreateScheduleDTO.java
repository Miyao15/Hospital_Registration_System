package com.hospital.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateScheduleDTO {
    
    // 管理员端使用的字段
    private String doctorId;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    private LocalDate endDate; // 可选，如果为空则只创建单天
    
    // 管理员端使用 - 时间段配置列表
    private List<TimeSlotConfig> timeSlots;
    
    // 管理员端使用 - 时间段类型列表 (兼容旧版本)
    private List<String> periods;
    
    // 管理员端使用 - 星期几列表 (0=周日, 1=周一, ..., 6=周六)
    private List<Integer> weekdays;
    
    // 医生端使用的字段
    private Boolean hasMorning = true;
    private Boolean hasAfternoon = true;
    
    private LocalTime morningStartTime;
    private LocalTime morningEndTime;
    private Integer morningSlots;
    
    private LocalTime afternoonStartTime;
    private LocalTime afternoonEndTime;
    private Integer afternoonSlots;
    
    private Integer eveningSlots; // 晚间号源数量
    
    private String scheduleType; // "TYPE1" 或 "TYPE2"，用于快速选择预设
    
    /**
     * 时间段配置内部类
     */
    @Data
    public static class TimeSlotConfig {
        private String period; // MORNING, AFTERNOON, EVENING
        private String startTime; // HH:mm 或 HH:mm:ss
        private String endTime;
        private Integer totalSlots;
    }
}
