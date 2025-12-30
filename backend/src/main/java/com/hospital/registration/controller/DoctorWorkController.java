package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.DoctorAppointmentDTO;
import com.hospital.registration.service.DoctorAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/doctor/work")
@RequiredArgsConstructor
public class DoctorWorkController {
    
    private final DoctorAppointmentService doctorAppointmentService;
    
    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @GetMapping("/appointments/today")
    public ApiResponse<List<DoctorAppointmentDTO>> getTodayAppointments() {
        String userId = getCurrentUserId();
        log.info("获取今日预约 - userId: {}", userId);
        return ApiResponse.success(doctorAppointmentService.getTodayAppointments(userId));
    }
    
    @GetMapping("/appointments")
    public ApiResponse<List<DoctorAppointmentDTO>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String userId = getCurrentUserId();
        log.info("获取指定日期预约 - userId: {}, date: {}", userId, date);
        return ApiResponse.success(doctorAppointmentService.getAppointmentsByDate(userId, date));
    }
    
    @PostMapping("/appointments/{id}/check-in")
    public ApiResponse<Void> markCheckedIn(@PathVariable String id) {
        String userId = getCurrentUserId();
        log.info("签到 - userId: {}, appointmentId: {}", userId, id);
        doctorAppointmentService.markCheckedIn(id, userId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/appointments/{id}/complete")
    public ApiResponse<Void> markCompleted(@PathVariable String id) {
        String userId = getCurrentUserId();
        log.info("完成就诊 - userId: {}, appointmentId: {}", userId, id);
        doctorAppointmentService.markCompleted(id, userId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/appointments/{id}/no-show")
    public ApiResponse<Void> markNoShow(@PathVariable String id) {
        String userId = getCurrentUserId();
        log.info("标记爽约 - userId: {}, appointmentId: {}", userId, id);
        doctorAppointmentService.markNoShow(id, userId);
        return ApiResponse.success(null);
    }
}
