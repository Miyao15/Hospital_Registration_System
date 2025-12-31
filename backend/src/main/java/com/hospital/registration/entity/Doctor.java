package com.hospital.registration.entity;

import com.hospital.registration.enums.DoctorTitle;
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
@Table(name = "doctors", indexes = {
    @Index(name = "idx_doctors_employee_id", columnList = "employee_id"),
    @Index(name = "idx_doctors_license_number", columnList = "license_number"),
    @Index(name = "idx_doctors_department_id", columnList = "department_id")
})
public class Doctor {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, unique = true, nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "employee_id", length = 20, unique = true, nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorTitle title;

    @Column(name = "department_id", length = 36, nullable = false)
    private String departmentId;

    @Column(columnDefinition = "TEXT")
    private String specialty;

    @Column(name = "license_number", length = 50, unique = true, nullable = false)
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(name = "schedule_info", length = 255)
    private String scheduleInfo;

    @Column(name = "hospital_id", length = 36)
    private String hospitalId;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by", length = 36)
    private String approvedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
