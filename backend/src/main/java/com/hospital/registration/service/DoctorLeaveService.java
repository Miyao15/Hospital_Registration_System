package com.hospital.registration.service;

import com.hospital.registration.dto.CreateLeaveDTO;
import com.hospital.registration.dto.DoctorLeaveDTO;
import com.hospital.registration.entity.Appointment;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.DoctorLeave;
import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.enums.LeaveStatus;
import com.hospital.registration.enums.NotificationType;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AppointmentRepository;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorLeaveRepository;
import com.hospital.registration.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorLeaveService {
    
    private final DoctorLeaveRepository leaveRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public DoctorLeaveDTO applyLeave(String userId, CreateLeaveDTO dto) {
        // 通过 userId 获取医生信息
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("请假开始日期不能早于今天");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        
        // 检查是否有重叠的待审批请假
        List<DoctorLeave> overlapping = leaveRepository.findOverlappingPendingLeaves(
                doctor.getId(), dto.getStartDate(), dto.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BusinessException("该时间段已有待审批的请假申请");
        }
        
        DoctorLeave leave = new DoctorLeave();
        leave.setDoctorId(doctor.getId());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        
        leave = leaveRepository.save(leave);
        final String[] departmentName = {""};
        if (doctor.getDepartmentId() != null) {
            departmentRepository.findById(doctor.getDepartmentId())
                    .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
        }
        return convertToDTO(leave, doctor.getName(), departmentName[0]);
    }
    
    public Page<DoctorLeaveDTO> getMyLeaves(String userId, int page, int size) {
        // 通过 userId 获取医生信息
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        
        return leaveRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId(), pageable)
                .map(leave -> {
                    final String[] departmentName = {""};
                    if (doctor.getDepartmentId() != null) {
                        departmentRepository.findById(doctor.getDepartmentId())
                                .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
                    }
                    return convertToDTO(leave, doctor.getName(), departmentName[0]);
                });
    }
    
    /**
     * 撤销请假申请
     */
    @Transactional
    public void cancelLeave(String leaveId, String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        DoctorLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BusinessException("请假记录不存在"));
        
        // 验证是否是本人的请假记录
        if (!leave.getDoctorId().equals(doctor.getId())) {
            throw new BusinessException("无权操作此请假记录");
        }
        
        // 只有待审批状态才能撤销
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessException("该请假申请已处理，无法撤销");
        }
        
        leave.setStatus(LeaveStatus.CANCELLED);
        leaveRepository.save(leave);
    }
    
    public Page<DoctorLeaveDTO> getPendingLeaves(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return leaveRepository.findByStatusOrderByCreatedAtDesc(LeaveStatus.PENDING, pageable)
                .map(leave -> {
                    Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
                    String doctorName = doctor != null ? doctor.getName() : "";
                    final String[] departmentName = {""};
                    if (doctor != null && doctor.getDepartmentId() != null) {
                        departmentRepository.findById(doctor.getDepartmentId())
                                .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
                    }
                    return convertToDTO(leave, doctorName, departmentName[0]);
                });
    }
    
    /**
     * 获取所有请假记录（支持按状态筛选）
     */
    public Page<DoctorLeaveDTO> getAllLeaves(LeaveStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (status != null) {
            return leaveRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                    .map(leave -> {
                        Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
                        String doctorName = doctor != null ? doctor.getName() : "";
                        final String[] departmentName = {""};
                        if (doctor != null && doctor.getDepartmentId() != null) {
                            departmentRepository.findById(doctor.getDepartmentId())
                                    .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
                        }
                        return convertToDTO(leave, doctorName, departmentName[0]);
                    });
        } else {
            return leaveRepository.findAll(pageable)
                    .map(leave -> {
                        Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
                        String doctorName = doctor != null ? doctor.getName() : "";
                        final String[] departmentName = {""};
                        if (doctor != null && doctor.getDepartmentId() != null) {
                            departmentRepository.findById(doctor.getDepartmentId())
                                    .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
                        }
                        return convertToDTO(leave, doctorName, departmentName[0]);
                    });
        }
    }
    
    @Transactional
    public DoctorLeaveDTO approveLeave(String leaveId, String adminId) {
        DoctorLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BusinessException("请假记录不存在"));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessException("该请假申请已处理");
        }
        
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(adminId);
        leave.setApprovedAt(LocalDateTime.now());
        leave = leaveRepository.save(leave);
        
        // 通知医生
        Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
        if (doctor != null && doctor.getUserId() != null) {
            notificationService.sendNotification(
                    doctor.getUserId(),
                    "请假申请已批准",
                    String.format("您的请假申请（%s 至 %s）已被批准", 
                            leave.getStartDate(), leave.getEndDate()),
                    NotificationType.SYSTEM,
                    leaveId
            );
        }
        
        // 取消该时间段内的预约并通知患者
        cancelAppointmentsAndNotify(leave);
        
        String doctorName = doctor != null ? doctor.getName() : "";
        final String[] departmentName = {""};
        if (doctor != null && doctor.getDepartmentId() != null) {
            departmentRepository.findById(doctor.getDepartmentId())
                    .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
        }
        return convertToDTO(leave, doctorName, departmentName[0]);
    }
    
    @Transactional
    public DoctorLeaveDTO rejectLeave(String leaveId, String adminId, String rejectReason) {
        DoctorLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BusinessException("请假记录不存在"));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessException("该请假申请已处理");
        }
        
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(adminId);
        leave.setApprovedAt(LocalDateTime.now());
        leave.setRejectReason(rejectReason);
        leave = leaveRepository.save(leave);
        
        // 通知医生
        Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
        if (doctor != null && doctor.getUserId() != null) {
            notificationService.sendNotification(
                    doctor.getUserId(),
                    "请假申请被拒绝",
                    String.format("您的请假申请（%s 至 %s）被拒绝，原因：%s", 
                            leave.getStartDate(), leave.getEndDate(), rejectReason),
                    NotificationType.SYSTEM,
                    leaveId
            );
        }
        
        String doctorName = doctor != null ? doctor.getName() : "";
        final String[] departmentName = {""};
        if (doctor != null && doctor.getDepartmentId() != null) {
            departmentRepository.findById(doctor.getDepartmentId())
                    .ifPresent(dept -> departmentName[0] = dept.getName() != null ? dept.getName() : "");
        }
        return convertToDTO(leave, doctorName, departmentName[0]);
    }
    
    private void cancelAppointmentsAndNotify(DoctorLeave leave) {
        LocalDate current = leave.getStartDate();
        while (!current.isAfter(leave.getEndDate())) {
            List<Appointment> appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentDateOrderByPeriodAsc(leave.getDoctorId(), current);
            
            for (Appointment apt : appointments) {
                if (apt.getStatus() == AppointmentStatus.PENDING) {
                    apt.setStatus(AppointmentStatus.CANCELLED);
                    apt.setCancelReason("医生停诊");
                    appointmentRepository.save(apt);
                    
                    // 通知患者
                    notificationService.sendNotification(
                            apt.getPatientId(),
                            "预约已取消",
                            String.format("您在 %s 的预约因医生停诊已被取消，请重新预约", apt.getAppointmentDate()),
                            NotificationType.APPOINTMENT_CANCELLED,
                            apt.getId()
                    );
                }
            }
            current = current.plusDays(1);
        }
    }
    
    private DoctorLeaveDTO convertToDTO(DoctorLeave leave, String doctorName) {
        return convertToDTO(leave, doctorName, "");
    }
    
    private DoctorLeaveDTO convertToDTO(DoctorLeave leave, String doctorName, String departmentName) {
        DoctorLeaveDTO dto = new DoctorLeaveDTO();
        dto.setId(leave.getId());
        dto.setDoctorId(leave.getDoctorId());
        dto.setDoctorName(doctorName);
        dto.setDepartmentName(departmentName);
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        
        // 计算请假天数（包含开始和结束日期）
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            dto.setDays((int) (daysBetween + 1));  // +1 因为包含开始和结束日期
        } else {
            dto.setDays(0);
        }
        
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus().name());
        dto.setStatusName(leave.getStatus().getDisplayName());
        dto.setApprovedBy(leave.getApprovedBy());
        dto.setApprovedAt(leave.getApprovedAt());
        dto.setRejectReason(leave.getRejectReason());
        dto.setReviewNote(leave.getRejectReason());  // 将拒绝原因作为审批意见
        dto.setCreatedAt(leave.getCreatedAt());
        return dto;
    }
}
