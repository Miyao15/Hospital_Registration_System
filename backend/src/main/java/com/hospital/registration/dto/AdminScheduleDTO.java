package com.hospital.registration.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminScheduleDTO {
    private String id;
    private String doctorId;
    private String doctorName;
    private String departmentName;
    private String scheduleDate;  // yyyy-MM-dd
    private Boolean isWorking;
    private List<TimeSlotDTO> timeSlots;
    private Integer totalSlots;
    private Integer remainingSlots;
    private Integer appointmentCount;
}

