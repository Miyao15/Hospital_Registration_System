package com.hospital.registration.controller;

import com.hospital.registration.dto.*;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.service.DoctorProfileService;
import com.hospital.registration.service.DoctorReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Slf4j
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
        log.info("GET /api/doctors called with page={}, size={}", page, size);
        Page<DoctorListDTO> result = doctorProfileService.getAllDoctors(page, size);
        log.info("Returning {} doctors", result.getTotalElements());
        return ApiResponse.success(result);
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
            @RequestParam(required = false) String medicalItemId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/doctors/search called with keyword={}, departmentId={}, title={}, medicalItemId={}, region={}, city={}", 
                keyword, departmentId, title, medicalItemId, region, city);
        DoctorSearchDTO searchDTO = new DoctorSearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setDepartmentId(departmentId);
        searchDTO.setTitle(title);
        searchDTO.setMedicalItemId(medicalItemId);
        searchDTO.setRegion(region);
        searchDTO.setCity(city);
        searchDTO.setProvince(province);
        searchDTO.setDistrict(district);
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        Page<DoctorListDTO> result = doctorProfileService.searchDoctors(searchDTO);
        log.info("Search returned {} doctors", result.getTotalElements());
        return ApiResponse.success(result);
    }
    
    @GetMapping("/{id}/reviews")
    public ApiResponse<Page<DoctorReviewDTO>> getDoctorReviews(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(doctorReviewService.getDoctorReviews(id, page, size));
    }

    @GetMapping("/top")
    public ApiResponse<List<DoctorListDTO>> getTopDoctors(@RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit) {
        log.info("GET /api/doctors/top called with limit: {}", limit);
        try {
            List<DoctorListDTO> topDoctors = doctorProfileService.getTopDoctors(limit);
            log.info("Controller: Returning {} top doctors", topDoctors != null ? topDoctors.size() : 0);
            if (topDoctors != null && !topDoctors.isEmpty()) {
                log.info("Controller: First doctor: id={}, name={}, rating={}, reviewCount={}", 
                    topDoctors.get(0).getId(), 
                    topDoctors.get(0).getName(),
                    topDoctors.get(0).getRating(),
                    topDoctors.get(0).getReviewCount());
            } else {
                log.warn("Controller: topDoctors is empty or null!");
            }
            return ApiResponse.success(topDoctors != null ? topDoctors : new ArrayList<>());
        } catch (Exception e) {
            log.error("Controller: Error in getTopDoctors: {}", e.getMessage(), e);
            return ApiResponse.success(new ArrayList<>());
        }
    }
    
    @GetMapping("/specialty/{specialty}")
    public ApiResponse<List<DoctorListDTO>> getDoctorsBySpecialty(@PathVariable String specialty) {
        try {
            // 统一转大写处理枚举匹配，防止 500 错误
            List<DoctorListDTO> doctors = doctorProfileService.getDoctorsBySpecialty(specialty.toUpperCase());
            return ApiResponse.success(doctors != null ? doctors : new ArrayList<>());
        } catch (Exception e) {
            log.error("Error fetching doctors by specialty: {}", specialty, e);
            // 捕获异常返回空列表，确保前端不崩溃
            return ApiResponse.success(new ArrayList<>());
        }
    }
    
    // 调试接口：检查数据库中的医生数量
    @GetMapping("/debug/count")
    public ApiResponse<Map<String, Object>> getDoctorCount() {
        log.info("========== DEBUG: 开始查询医生数量 ==========");
        try {
            long count = doctorProfileService.getTotalDoctorCount();
            List<Doctor> rawDoctors = doctorProfileService.debugFindAllRaw();
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", count);
            result.put("rawDoctorsCount", rawDoctors != null ? rawDoctors.size() : 0);
            if (rawDoctors != null && !rawDoctors.isEmpty()) {
                result.put("firstDoctorId", rawDoctors.get(0).getId());
                result.put("firstDoctorName", rawDoctors.get(0).getName());
            }
            log.info("========== DEBUG: 查询成功，医生数量 = {} ==========", count);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("========== DEBUG: 查询失败 ==========");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            log.error("完整堆栈:", e);
            return ApiResponse.error("ERROR", "查询失败: " + e.getMessage());
        }
    }
    
    // 调试端点 - 测试原始 findAll
    @GetMapping("/debug/raw")
    public ApiResponse<Object> debugRaw() {
        log.info("========== DEBUG RAW: 开始原始查询 ==========");
        try {
            var doctors = doctorProfileService.debugFindAllRaw();
            log.info("========== DEBUG RAW: 查询成功，返回 {} 条记录 ==========", doctors.size());
            return ApiResponse.success(doctors);
        } catch (Exception e) {
            log.error("========== DEBUG RAW: 查询失败 ==========");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            log.error("完整堆栈:", e);
            return ApiResponse.success("原始查询失败: " + e.getMessage());
        }
    }
    
    // 调试端点 - 执行原生 SQL 查询
    @GetMapping("/debug/sql")
    public ApiResponse<Object> debugSql() {
        log.info("========== DEBUG SQL: 执行原生SQL查询 ==========");
        try {
            var result = doctorProfileService.debugNativeQuery();
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("DEBUG SQL 失败:", e);
            return ApiResponse.success("SQL查询失败: " + e.getMessage());
        }
    }
}
