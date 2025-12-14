package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.AvailableDateDTO;
import com.hospital.registration.dto.TimeSlotDTO;
import com.hospital.registration.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @GetMapping("/doctor/{doctorId}/dates")
    public ApiResponse<List<AvailableDateDTO>> getAvailableDates(
            @PathVariable String doctorId,
            @RequestParam(defaultValue = "14") int days) {
        return ApiResponse.success(scheduleService.getAvailableDates(doctorId, days));
    }
    
    @GetMapping("/doctor/{doctorId}/slots")
    public ApiResponse<List<TimeSlotDTO>> getTimeSlots(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(scheduleService.getTimeSlots(doctorId, date));
    }
}
