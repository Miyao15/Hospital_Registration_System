package com.hospital.registration.controller;

import com.hospital.registration.dto.HospitalDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    
    private final HospitalService hospitalService;
    
    @GetMapping
    public ApiResponse<List<HospitalDTO>> getAllHospitals(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province) {
        List<HospitalDTO> hospitals;
        
        if (region != null && city != null) {
            hospitals = hospitalService.getHospitalsByRegionAndCity(region, city);
        } else if (region != null) {
            hospitals = hospitalService.getHospitalsByRegion(region);
        } else if (city != null) {
            hospitals = hospitalService.getHospitalsByCity(city);
        } else if (province != null) {
            hospitals = hospitalService.getHospitalsByProvince(province);
        } else {
            hospitals = hospitalService.getAllHospitals();
        }
        
        return ApiResponse.success(hospitals);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<HospitalDTO> getHospitalById(@PathVariable String id) {
        return hospitalService.getHospitalById(id)
            .map(ApiResponse::success)
            .orElse(ApiResponse.error("NOT_FOUND", "医院不存在"));
    }
    
    @PostMapping
    public ApiResponse<HospitalDTO> createHospital(@RequestBody HospitalDTO dto) {
        HospitalDTO created = hospitalService.createHospital(dto);
        return ApiResponse.success(created);
    }
}
