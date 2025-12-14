package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.StatisticsDTO;
import com.hospital.registration.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/dashboard")
    public ApiResponse<StatisticsDTO> getDashboardStatistics() {
        return ApiResponse.success(statisticsService.getDashboardStatistics());
    }
    
    @GetMapping("/trend")
    public ApiResponse<Map<String, Object>> getAppointmentTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(statisticsService.getAppointmentTrend(days));
    }
}
