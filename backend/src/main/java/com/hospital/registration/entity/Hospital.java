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
@Table(name = "hospitals")
public class Hospital {
    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    // 经度
    @Column(precision = 10)
    private Double longitude;

    // 纬度
    @Column(precision = 10)
    private Double latitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
