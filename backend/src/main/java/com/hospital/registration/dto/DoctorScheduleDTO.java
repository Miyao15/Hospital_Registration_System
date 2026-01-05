package com.hospital.registration.dto;

import lombok.Data;

@Data
public class DoctorScheduleDTO {
    private String id;
    private String scheduleDate;  // yyyy-MM-dd
    private Boolean isWorking;
    private Boolean hasMorning;
    private Boolean hasAfternoon;
    private String morningTime;
    private String afternoonTime;
    private Integer morningSlots;      // 上午剩余号源
    private Integer afternoonSlots;    // 下午剩余号源
    private Integer morningTotalSlots; // 上午总号源
    private Integer afternoonTotalSlots; // 下午总号源
    private Integer morningBooked;     // 上午已预约数
    private Integer afternoonBooked;   // 下午已预约数
    private Integer appointmentCount;  // 当天总预约数
}

