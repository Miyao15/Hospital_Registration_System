package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReviewDTO {
    private String id;
    private String patientName;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
