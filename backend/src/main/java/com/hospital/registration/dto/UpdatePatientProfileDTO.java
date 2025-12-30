package com.hospital.registration.dto;

import com.hospital.registration.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePatientProfileDTO {
    private String name;
    private String idCard;
    private Gender gender;
    private LocalDate birthDate;
    private String medicalHistory;
    private String allergyHistory;
    private String emergencyContact;
    private String emergencyPhone;
}

