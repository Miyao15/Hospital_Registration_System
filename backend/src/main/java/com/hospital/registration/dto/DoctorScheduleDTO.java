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
    private Integer morningSlots;
    private Integer afternoonSlots;
    private Integer appointmentCount;
}

