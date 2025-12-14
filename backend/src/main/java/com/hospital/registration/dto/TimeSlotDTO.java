package com.hospital.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private String id;
    private String period;
    private String periodName;
    private String timeRange;
    private Integer totalSlots;
    private Integer remainingSlots;
    private Boolean available;
}
