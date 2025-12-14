package com.hospital.registration.controller;

import com.hospital.registration.dto.*;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DoctorProfileService;
import com.hospital.registration.service.DoctorReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorProfileService doctorProfileService;
    private final DoctorReviewService doctorReviewService;
    
    @GetMapping
    public ApiResponse<Page<DoctorListDTO>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(doctorProfileService.getAllDoctors(page, size));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<DoctorDetailDTO> getDoctorById(@PathVariable String id) {
        return ApiResponse.success(doctorProfileService.getDoctorById(id));
    }
    
    @GetMapping("/search")
    public ApiResponse<Page<DoctorListDTO>> searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        DoctorSearchDTO searchDTO = new DoctorSearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setDepartmentId(departmentId);
        searchDTO.setTitle(title);
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        return ApiResponse.success(doctorProfileService.searchDoctors(searchDTO));
    }
    
    @GetMapping("/{id}/reviews")
    public ApiResponse<Page<DoctorReviewDTO>> getDoctorReviews(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(doctorReviewService.getDoctorReviews(id, page, size));
    }
}
