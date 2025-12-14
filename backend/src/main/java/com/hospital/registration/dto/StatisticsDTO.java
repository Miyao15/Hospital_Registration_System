package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    
    // 今日统计
    private Integer todayAppointments;
    private Integer todayCheckedIn;
    private Integer todayCompleted;
    private Integer todayNoShow;
    
    // 总体统计
    private Integer totalPatients;
    private Integer totalDoctors;
    private Integer totalAppointments;
    
    // 就诊率
    private Double checkInRate;
    private Double noShowRate;
    
    // 科室预约排行
    private List<Map<String, Object>> departmentRanking;
    
    // 医生工作量排行
    private List<Map<String, Object>> doctorWorkload;
}
