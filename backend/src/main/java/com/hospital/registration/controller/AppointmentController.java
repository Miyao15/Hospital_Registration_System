package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.AppointmentDetailDTO;
import com.hospital.registration.dto.CreateAppointmentDTO;
import com.hospital.registration.dto.RescheduleAppointmentDTO;
import com.hospital.registration.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@lombok.extern.slf4j.Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ApiResponse<AppointmentDetailDTO> createAppointment(
            @Valid @RequestBody CreateAppointmentDTO dto) {
        // 从 SecurityContext 获取 userId（JWT filter 设置的 principal 是 userId 字符串）
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        log.info("收到预约请求 - userId: {}, dto: {}", userId, dto);
        try {
            AppointmentDetailDTO result = appointmentService.createAppointment(userId, dto);
            log.info("预约成功 - appointmentId: {}", result.getId());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("预约失败: ", e);
            throw e;
        }
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
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return ApiResponse.success(appointmentService.getMyAppointments(userId, status, page, size));
    }
    
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        appointmentService.cancelAppointment(id, userId, reason);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/{id}/reschedule")
    public ApiResponse<AppointmentDetailDTO> rescheduleAppointment(
            @PathVariable String id,
            @Valid @RequestBody RescheduleAppointmentDTO dto) {
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return ApiResponse.success(appointmentService.rescheduleAppointment(
                id, userId, dto.getNewTimeSlotId(), dto.getReason()));
    }
}
