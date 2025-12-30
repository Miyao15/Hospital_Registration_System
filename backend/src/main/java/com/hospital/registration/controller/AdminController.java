package com.hospital.registration.controller;

import com.hospital.registration.dto.CreateDepartmentDTO;
import com.hospital.registration.dto.CreateScheduleDTO;
import com.hospital.registration.dto.DepartmentDTO;
import com.hospital.registration.dto.DoctorLeaveDTO;
import com.hospital.registration.dto.request.UpdateDoctorProfileRequest;
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
import com.hospital.registration.service.AppointmentService;
import com.hospital.registration.dto.AppointmentDetailDTO;
import com.hospital.registration.dto.UserDetailDTO;
import com.hospital.registration.dto.AdminUpdateAppointmentDTO;
import com.hospital.registration.dto.request.UpdatePatientInfoRequest;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AdminDepartmentService adminDepartmentService;
    private final AdminScheduleService adminScheduleService;
    private final DoctorLeaveService doctorLeaveService;
    private final AppointmentService appointmentService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDetailDTO>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserDetailDTO> users = adminService.listUsers(page, size);
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

    @PutMapping("/doctors/{id}/profile")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateDoctorProfile(
            @PathVariable String id,
            @RequestBody UpdateDoctorProfileRequest request) {
        adminService.updateDoctorProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "医师资料已更新")));
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
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success(adminDepartmentService.getAllDepartments()));
    }
    
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
    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.hospital.registration.dto.AdminScheduleDTO>>> getSchedules(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminScheduleService.getSchedules(doctorId, departmentId, startDate, endDate, pageable)));
    }
    
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
    
    @GetMapping("/leaves")
    public ResponseEntity<ApiResponse<Page<DoctorLeaveDTO>>> getAllLeaves(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        com.hospital.registration.enums.LeaveStatus leaveStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                leaveStatus = com.hospital.registration.enums.LeaveStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // 无效的状态值，返回空结果
                return ResponseEntity.ok(ApiResponse.success(org.springframework.data.domain.Page.empty()));
            }
        }
        return ResponseEntity.ok(ApiResponse.success(doctorLeaveService.getAllLeaves(leaveStatus, page, size)));
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
    
    // 预约管理接口
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<Page<AppointmentDetailDTO>>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.getAllAppointments(status, appointmentDate, page, size)));
    }

    // 修改患者信息
    @PutMapping("/patients/{userId}/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> updatePatientInfo(
            @PathVariable String userId,
            @Valid @RequestBody UpdatePatientInfoRequest request) {
        adminService.updatePatientInfo(userId, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "患者信息已更新")));
    }

    // 管理员修改用户密码
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<ApiResponse<Map<String, String>>> adminChangeUserPassword(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("VALIDATION_ERROR", "新密码不能为空"));
        }
        adminService.adminChangeUserPassword(userId, newPassword);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "密码修改成功")));
    }

    // 管理员删除预约（取消预约）
    @PostMapping("/appointments/{id}/cancel")
    public ResponseEntity<ApiResponse<Map<String, String>>> adminCancelAppointment(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        appointmentService.adminCancelAppointment(id, reason);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "预约已取消")));
    }

    // 管理员修改预约信息
    @PutMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<AppointmentDetailDTO>> adminUpdateAppointment(
            @PathVariable String id,
            @Valid @RequestBody AdminUpdateAppointmentDTO request) {
        AppointmentDetailDTO result = appointmentService.adminUpdateAppointment(id, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
