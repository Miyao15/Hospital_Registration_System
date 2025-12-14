package com.hospital.registration.entity;

import com.hospital.registration.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patients_id_card", columnList = "id_card"),
    @Index(name = "idx_patients_name", columnList = "name")
})
public class Patient {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, unique = true, nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "id_card", length = 18, unique = true, nullable = false)
    private String idCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;

    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
