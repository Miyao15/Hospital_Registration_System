package com.hospital.registration.entity;

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
@Table(name = "schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"doctor_id", "schedule_date"})
})
public class Schedule {
    
    @Id
    private String id;
    
    @Column(name = "doctor_id", nullable = false)
    private String doctorId;
    
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;
    
    @Column(name = "is_working")
    private Boolean isWorking = true;
    
    @Column(length = 255)
    private String remark;
    
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
