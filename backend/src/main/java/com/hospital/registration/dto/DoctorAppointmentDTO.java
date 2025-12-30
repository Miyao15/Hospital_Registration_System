package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentDTO {
    private String id;
    private String appointmentNo;
    private String patientId;
    private String patientName;
    private String patientPhone;
    private String patientGender;
    private Integer patientAge;
    private LocalDate appointmentDate;
    private String period;
    private String periodName;
    private String timeRange;
    private String symptomDesc;
    private String medicalItemId;
    private String medicalItemName;
    private BigDecimal medicalItemPrice;
    private String status;
    private String statusName;
    private LocalDateTime createdAt;
}
