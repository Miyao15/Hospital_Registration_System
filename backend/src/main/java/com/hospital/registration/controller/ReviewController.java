package com.hospital.registration.controller;

import com.hospital.registration.dto.CreateReviewDTO;
import com.hospital.registration.dto.DoctorReviewDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DoctorReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final DoctorReviewService doctorReviewService;

    @PostMapping
    public ApiResponse<DoctorReviewDTO> createReview(@Valid @RequestBody CreateReviewDTO dto) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("创建评价 - userId: {}, doctorId: {}", userId, dto.getDoctorId());
        DoctorReviewDTO result = doctorReviewService.createReview(userId, dto);
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<Page<DoctorReviewDTO>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<DoctorReviewDTO> reviews = doctorReviewService.getMyReviews(userId, page, size);
        return ApiResponse.success(reviews);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable String id) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        doctorReviewService.deleteReview(userId, id);
        return ApiResponse.success(null);
    }
    
    /**
     * 公开API - 获取某个医生的评价列表
     */
    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<Page<DoctorReviewDTO>> getDoctorReviews(
            @PathVariable String doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取医生评价 - doctorId: {}, page: {}, size: {}", doctorId, page, size);
        Page<DoctorReviewDTO> reviews = doctorReviewService.getDoctorReviews(doctorId, page, size);
        return ApiResponse.success(reviews);
    }
    
    /**
     * 公开API - 获取某个医生的评价统计
     */
    @GetMapping("/doctor/{doctorId}/stats")
    public ApiResponse<Map<String, Object>> getDoctorReviewStats(@PathVariable String doctorId) {
        log.info("获取医生评价统计 - doctorId: {}", doctorId);
        Map<String, Object> stats = doctorReviewService.getDoctorReviewStats(doctorId);
        return ApiResponse.success(stats);
    }
}

