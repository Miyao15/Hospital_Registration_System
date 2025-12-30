package com.hospital.registration.dto.request;

import com.hospital.registration.enums.Gender;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePatientInfoRequest {
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String medicalHistory;
    private String allergyHistory;
    private String emergencyContact;
    private String emergencyPhone;
}

