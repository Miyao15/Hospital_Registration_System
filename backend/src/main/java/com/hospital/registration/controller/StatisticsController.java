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
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StatisticsDTO> getDashboardStatistics() {
        return ApiResponse.success(statisticsService.getDashboardStatistics());
    }
    
    @GetMapping("/trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getAppointmentTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(statisticsService.getAppointmentTrend(days));
    }
    
    /**
     * 公开API - 获取首页统计数据（无需登录）
     */
    @GetMapping("/public")
    public ApiResponse<Map<String, Object>> getPublicStatistics() {
        return ApiResponse.success(statisticsService.getPublicStatistics());
    }
}
