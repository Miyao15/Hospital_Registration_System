package com.hospital.registration.controller;

import com.hospital.registration.dto.CreateLeaveDTO;
import com.hospital.registration.dto.DoctorLeaveDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DoctorLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor/leaves")
@RequiredArgsConstructor
public class DoctorLeaveController {
    
    private final DoctorLeaveService leaveService;
    
    @PostMapping
    public ApiResponse<DoctorLeaveDTO> applyLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateLeaveDTO dto) {
        String doctorId = userDetails.getUsername();
        return ApiResponse.success(leaveService.applyLeave(doctorId, dto));
    }
    
    @GetMapping("/my")
    public ApiResponse<Page<DoctorLeaveDTO>> getMyLeaves(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String doctorId = userDetails.getUsername();
        return ApiResponse.success(leaveService.getMyLeaves(doctorId, page, size));
    }
}
