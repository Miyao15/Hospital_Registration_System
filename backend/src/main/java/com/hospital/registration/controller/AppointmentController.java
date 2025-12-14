package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.AppointmentDetailDTO;
import com.hospital.registration.dto.CreateAppointmentDTO;
import com.hospital.registration.dto.RescheduleAppointmentDTO;
import com.hospital.registration.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ApiResponse<AppointmentDetailDTO> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAppointmentDTO dto) {
        // 从 UserDetails 获取患者ID（需要根据实际实现调整）
        String patientId = userDetails.getUsername(); // 这里假设 username 是 patientId
        return ApiResponse.success(appointmentService.createAppointment(patientId, dto));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<AppointmentDetailDTO> getAppointmentById(@PathVariable String id) {
        return ApiResponse.success(appointmentService.getAppointmentById(id));
    }
    
    @GetMapping("/no/{appointmentNo}")
    public ApiResponse<AppointmentDetailDTO> getAppointmentByNo(@PathVariable String appointmentNo) {
        return ApiResponse.success(appointmentService.getAppointmentByNo(appointmentNo));
    }
    
    @GetMapping("/my")
    public ApiResponse<Page<AppointmentDetailDTO>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String patientId = userDetails.getUsername();
        return ApiResponse.success(appointmentService.getMyAppointments(patientId, status, page, size));
    }
    
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        String patientId = userDetails.getUsername();
        appointmentService.cancelAppointment(id, patientId, reason);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/{id}/reschedule")
    public ApiResponse<AppointmentDetailDTO> rescheduleAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody RescheduleAppointmentDTO dto) {
        String patientId = userDetails.getUsername();
        return ApiResponse.success(appointmentService.rescheduleAppointment(
                id, patientId, dto.getNewTimeSlotId(), dto.getReason()));
    }
}
