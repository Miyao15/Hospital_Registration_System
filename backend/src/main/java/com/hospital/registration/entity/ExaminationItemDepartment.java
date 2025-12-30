package com.hospital.registration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "examination_item_departments", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"examination_item_id", "department_id"}))
public class ExaminationItemDepartment {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "examination_item_id", nullable = false, length = 36)
    private String examinationItemId;
    
    @Column(name = "department_id", nullable = false, length = 36)
    private String departmentId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}

