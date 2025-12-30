package com.hospital.registration.entity;

import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.enums.TimePeriod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    private String id;
    
    @Column(name = "appointment_no", nullable = false, unique = true, length = 20)
    private String appointmentNo;
    
    @Column(name = "patient_id", nullable = false)
    private String patientId;
    
    @Column(name = "doctor_id", nullable = false)
    private String doctorId;
    
    @Column(name = "department_id", nullable = false)
    private String departmentId;
    
    @Column(name = "time_slot_id", nullable = false)
    private String timeSlotId;
    
    @Column(name = "medical_item_id")
    private String medicalItemId;
    
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimePeriod period;
    
    @Column(name = "patient_name", nullable = false, length = 50)
    private String patientName;
    
    @Column(name = "patient_phone", nullable = false, length = 20)
    private String patientPhone;
    
    @Column(name = "symptom_desc", columnDefinition = "TEXT")
    private String symptomDesc;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
