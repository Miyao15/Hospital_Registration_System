package com.hospital.registration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admins", indexes = {
    @Index(name = "idx_admins_employee_id", columnList = "employee_id")
})
public class Admin {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, unique = true, nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "employee_id", length = 20, unique = true, nullable = false)
    private String employeeId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
