package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.DoctorAppointmentDTO;
import com.hospital.registration.service.DoctorAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor/work")
@RequiredArgsConstructor
public class DoctorWorkController {
    
    private final DoctorAppointmentService doctorAppointmentService;
    
    @GetMapping("/appointments/today")
    public ApiResponse<List<DoctorAppointmentDTO>> getTodayAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        String doctorId = userDetails.getUsername();
        return ApiResponse.success(doctorAppointmentService.getTodayAppointments(doctorId));
    }
    
    @GetMapping("/appointments")
    public ApiResponse<List<DoctorAppointmentDTO>> getAppointmentsByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String doctorId = userDetails.getUsername();
        return ApiResponse.success(doctorAppointmentService.getAppointmentsByDate(doctorId, date));
    }
    
    @PostMapping("/appointments/{id}/check-in")
    public ApiResponse<Void> markCheckedIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String doctorId = userDetails.getUsername();
        doctorAppointmentService.markCheckedIn(id, doctorId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/appointments/{id}/complete")
    public ApiResponse<Void> markCompleted(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String doctorId = userDetails.getUsername();
        doctorAppointmentService.markCompleted(id, doctorId);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/appointments/{id}/no-show")
    public ApiResponse<Void> markNoShow(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String doctorId = userDetails.getUsername();
        doctorAppointmentService.markNoShow(id, doctorId);
        return ApiResponse.success(null);
    }
}
