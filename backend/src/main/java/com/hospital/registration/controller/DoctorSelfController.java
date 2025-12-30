package com.hospital.registration.controller;

import com.hospital.registration.dto.DoctorDetailDTO;
import com.hospital.registration.dto.DoctorReviewDTO;
import com.hospital.registration.dto.DoctorScheduleDTO;
import com.hospital.registration.dto.UpdateDoctorProfileDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DoctorProfileService;
import com.hospital.registration.service.DoctorReviewService;
import com.hospital.registration.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorSelfController {

    private final DoctorProfileService doctorProfileService;
    private final DoctorReviewService doctorReviewService;
    private final ScheduleService scheduleService;

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取当前登录医生的个人资料
     */
    @GetMapping("/profile")
    public ApiResponse<DoctorDetailDTO> getProfile() {
        String userId = getCurrentUserId();
        log.info("获取医生个人资料 - userId: {}", userId);
        return ApiResponse.success(doctorProfileService.getDoctorProfileByUserId(userId));
    }

    /**
     * 更新当前登录医生的个人资料
     */
    @PutMapping("/profile")
    public ApiResponse<DoctorDetailDTO> updateProfile(@Valid @RequestBody UpdateDoctorProfileDTO dto) {
        String userId = getCurrentUserId();
        log.info("更新医生个人资料 - userId: {}", userId);
        return ApiResponse.success(doctorProfileService.updateDoctorProfile(userId, dto));
    }

    /**
     * 获取当前登录医生收到的评价
     */
    @GetMapping("/reviews/my")
    public ApiResponse<Page<DoctorReviewDTO>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter) {
        String userId = getCurrentUserId();
        log.info("获取医生评价 - userId: {}", userId);
        return ApiResponse.success(doctorReviewService.getDoctorReviewsByUserId(userId, page, size, filter));
    }

    /**
     * 获取当前登录医生的评价统计
     */
    @GetMapping("/reviews/stats")
    public ApiResponse<Map<String, Object>> getReviewStats() {
        String userId = getCurrentUserId();
        log.info("获取医生评价统计 - userId: {}", userId);
        return ApiResponse.success(doctorReviewService.getDoctorReviewStatsByUserId(userId));
    }

    /**
     * 获取当前登录医生的排班信息
     */
    @GetMapping("/schedules")
    public ApiResponse<List<DoctorScheduleDTO>> getMySchedules(
            @RequestParam int year,
            @RequestParam int month) {
        String userId = getCurrentUserId();
        log.info("获取医生排班 - userId: {}, year: {}, month: {}", userId, year, month);
        return ApiResponse.success(scheduleService.getDoctorSchedulesByUserId(userId, year, month));
    }
}

