package com.hospital.registration.dto;

import lombok.Data;

@Data
public class AdminUpdateAppointmentDTO {
    private String timeSlotId;  // 新的时间段ID
    private String symptomDesc;  // 病情描述
    private String patientName;  // 患者姓名
    private String patientPhone;  // 患者电话
}

