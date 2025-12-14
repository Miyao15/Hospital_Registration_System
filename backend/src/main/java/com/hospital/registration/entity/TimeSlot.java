package com.hospital.registration.entity;

import com.hospital.registration.enums.TimePeriod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_slots", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"schedule_id", "period"})
})
public class TimeSlot {
    
    @Id
    private String id;
    
    @Column(name = "schedule_id", nullable = false)
    private String scheduleId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimePeriod period;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots = 20;
    
    @Column(name = "remaining_slots", nullable = false)
    private Integer remainingSlots = 20;
    
    @Version
    @Column(nullable = false)
    private Integer version = 0;
    
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
