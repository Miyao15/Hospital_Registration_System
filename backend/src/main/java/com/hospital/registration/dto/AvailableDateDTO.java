package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDateDTO {
    private LocalDate date;
    private String dayOfWeek;
    private Boolean available;
    private Integer totalSlots;
    private Integer remainingSlots;
}
