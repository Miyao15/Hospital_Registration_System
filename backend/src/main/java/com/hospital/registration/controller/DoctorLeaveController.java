package com.hospital.registration.controller;

import com.hospital.registration.dto.CreateLeaveDTO;
import com.hospital.registration.dto.DoctorLeaveDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DoctorLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/doctor/leaves")
@RequiredArgsConstructor
public class DoctorLeaveController {
    
    private final DoctorLeaveService leaveService;
    
    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @PostMapping
    public ApiResponse<DoctorLeaveDTO> applyLeave(@Valid @RequestBody CreateLeaveDTO dto) {
        String userId = getCurrentUserId();
        log.info("申请请假 - userId: {}", userId);
        return ApiResponse.success(leaveService.applyLeave(userId, dto));
    }
    
    @GetMapping("/my")
    public ApiResponse<Page<DoctorLeaveDTO>> getMyLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = getCurrentUserId();
        log.info("获取请假记录 - userId: {}", userId);
        return ApiResponse.success(leaveService.getMyLeaves(userId, page, size));
    }
    
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelLeave(@PathVariable String id) {
        String userId = getCurrentUserId();
        log.info("撤销请假 - userId: {}, leaveId: {}", userId, id);
        leaveService.cancelLeave(id, userId);
        return ApiResponse.success(null);
    }
}
