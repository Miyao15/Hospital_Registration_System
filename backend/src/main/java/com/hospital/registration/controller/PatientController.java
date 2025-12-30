package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.PatientProfileDTO;
import com.hospital.registration.dto.UpdatePatientProfileDTO;
import com.hospital.registration.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/profile")
    public ApiResponse<PatientProfileDTO> getProfile() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("获取患者个人资料 - userId: {}", userId);
        PatientProfileDTO profile = patientService.getPatientProfile(userId);
        return ApiResponse.success(profile);
    }

    @PutMapping("/profile")
    public ApiResponse<PatientProfileDTO> updateProfile(@Valid @RequestBody UpdatePatientProfileDTO dto) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("更新患者个人资料 - userId: {}", userId);
        PatientProfileDTO profile = patientService.updatePatientProfile(userId, dto);
        return ApiResponse.success(profile);
    }
}

