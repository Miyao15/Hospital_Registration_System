package com.hospital.registration.controller;

import com.hospital.registration.dto.CreateDepartmentDTO;
import com.hospital.registration.dto.CreateScheduleDTO;
import com.hospital.registration.dto.DepartmentDTO;
import com.hospital.registration.dto.DoctorLeaveDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.entity.Schedule;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.service.AdminDepartmentService;
import com.hospital.registration.service.AdminScheduleService;
import com.hospital.registration.service.AdminService;
import com.hospital.registration.service.DoctorLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AdminDepartmentService adminDepartmentService;
    private final AdminScheduleService adminScheduleService;
    private final DoctorLeaveService doctorLeaveService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> users = adminService.listUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/doctors/{id}/approve")
    public ResponseEntity<ApiResponse<Map<String, String>>> approveDoctor(
            @PathVariable String id,
            Authentication authentication) {
        String adminId = (String) authentication.getPrincipal();
        adminService.approveDoctor(id, adminId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "医师审批通过")));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleUserStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        UserStatus status = UserStatus.valueOf(request.get("status"));
        adminService.toggleUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "用户状态已更新")));
    }
    
    // 科室管理接口
    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody CreateDepartmentDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(adminDepartmentService.createDepartment(dto)));
    }
    
    @PutMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody CreateDepartmentDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(adminDepartmentService.updateDepartment(id, dto)));
    }
    
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteDepartment(@PathVariable String id) {
        adminDepartmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "科室已禁用")));
    }
    
    @PostMapping("/departments/{id}/enable")
    public ResponseEntity<ApiResponse<Map<String, String>>> enableDepartment(@PathVariable String id) {
        adminDepartmentService.enableDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "科室已启用")));
    }
    
    // 排班管理接口
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<List<Schedule>>> batchCreateSchedules(
            @Valid @RequestBody CreateScheduleDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(adminScheduleService.batchCreateSchedules(dto)));
    }
    
    @DeleteMapping("/schedules/{doctorId}/{date}")
    public ResponseEntity<ApiResponse<Map<String, String>>> cancelSchedule(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        adminScheduleService.cancelSchedule(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "排班已取消")));
    }
    
    @PutMapping("/time-slots/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateSlotCount(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request) {
        adminScheduleService.updateSlotCount(id, request.get("totalSlots"));
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "号源数量已更新")));
    }
    
    // 请假审批接口
    @GetMapping("/leaves/pending")
    public ResponseEntity<ApiResponse<Page<DoctorLeaveDTO>>> getPendingLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(doctorLeaveService.getPendingLeaves(page, size)));
    }
    
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<ApiResponse<DoctorLeaveDTO>> approveLeave(
            @PathVariable String id,
            Authentication authentication) {
        String adminId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(doctorLeaveService.approveLeave(id, adminId)));
    }
    
    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<ApiResponse<DoctorLeaveDTO>> rejectLeave(
            @PathVariable String id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String adminId = (String) authentication.getPrincipal();
        String reason = request.get("reason");
        return ResponseEntity.ok(ApiResponse.success(doctorLeaveService.rejectLeave(id, adminId, reason)));
    }
}
