package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailDTO {
    private String id;
    private String appointmentNo;
    private String doctorId;
    private String doctorName;
    private String doctorTitle;
    private String departmentId;
    private String departmentName;
    private String departmentLocation;
    private LocalDate appointmentDate;
    private String period;
    private String periodName;
    private String timeRange;
    private String patientName;
    private String patientPhone;
    private String symptomDesc;
    private String status;
    private String statusName;
    private LocalDateTime createdAt;
}
