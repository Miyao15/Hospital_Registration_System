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
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public DoctorLeaveDTO applyLeave(String doctorId, CreateLeaveDTO dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("医生不存在"));
        
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("请假开始日期不能早于今天");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        
        // 检查是否有重叠的待审批请假
        List<DoctorLeave> overlapping = leaveRepository.findOverlappingPendingLeaves(
                doctorId, dto.getStartDate(), dto.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BusinessException("该时间段已有待审批的请假申请");
        }
        
        DoctorLeave leave = new DoctorLeave();
        leave.setDoctorId(doctorId);
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        
        leave = leaveRepository.save(leave);
        return convertToDTO(leave, doctor.getName());
    }
    
    public Page<DoctorLeaveDTO> getMyLeaves(String doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        String doctorName = doctor != null ? doctor.getName() : "";
        
        return leaveRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId, pageable)
                .map(leave -> convertToDTO(leave, doctorName));
    }
    
    public Page<DoctorLeaveDTO> getPendingLeaves(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return leaveRepository.findByStatusOrderByCreatedAtDesc(LeaveStatus.PENDING, pageable)
                .map(leave -> {
                    Doctor doctor = doctorRepository.findById(leave.getDoctorId()).orElse(null);
                    return convertToDTO(leave, doctor != null ? doctor.getName() : "");
                });
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
        
        return convertToDTO(leave, doctor != null ? doctor.getName() : "");
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
        
        return convertToDTO(leave, doctor != null ? doctor.getName() : "");
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
        DoctorLeaveDTO dto = new DoctorLeaveDTO();
        dto.setId(leave.getId());
        dto.setDoctorId(leave.getDoctorId());
        dto.setDoctorName(doctorName);
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus().name());
        dto.setStatusName(leave.getStatus().getDisplayName());
        dto.setApprovedBy(leave.getApprovedBy());
        dto.setApprovedAt(leave.getApprovedAt());
        dto.setRejectReason(leave.getRejectReason());
        dto.setCreatedAt(leave.getCreatedAt());
        return dto;
    }
}
