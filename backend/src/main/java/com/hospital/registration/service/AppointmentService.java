package com.hospital.registration.service;

import com.hospital.registration.dto.AdminUpdateAppointmentDTO;
import com.hospital.registration.dto.AppointmentDetailDTO;
import com.hospital.registration.dto.CreateAppointmentDTO;
import com.hospital.registration.entity.*;
import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientRepository patientRepository;
    private final MedicalItemRepository medicalItemRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public AppointmentDetailDTO createAppointment(String userId, CreateAppointmentDTO dto) {
        log.info("创建预约 - userId: {}, doctorId: {}, timeSlotId: {}", userId, dto.getDoctorId(), dto.getTimeSlotId());
        
        // 通过 userId 查找患者信息
        Patient patient = patientRepository.findByUserId(userId).orElse(null);
        if (patient == null) {
            log.error("找不到患者信息 - userId: {}", userId);
            throw new BusinessException("患者信息不存在，请先在个人中心完善资料");
        }
        log.info("找到患者 - patientId: {}, name: {}", patient.getId(), patient.getName());
        
        String patientId = patient.getId();
        
        // 验证医生
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new BusinessException("医生不存在"));
        
        // 验证时间段
        TimeSlot timeSlot = timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new BusinessException("时间段不存在"));
        
        // 获取排班信息
        Schedule schedule = scheduleRepository.findById(timeSlot.getScheduleId())
                .orElseThrow(() -> new BusinessException("排班信息不存在"));
        
        // 检查是否已预约
        int existingCount = appointmentRepository.countExistingAppointment(
                patientId, dto.getDoctorId(), schedule.getScheduleDate());
        if (existingCount > 0) {
            throw new BusinessException("您已预约该医生该日期的号源");
        }
        
        // 使用乐观锁扣减号源
        int updated = timeSlotRepository.decrementSlot(timeSlot.getId(), timeSlot.getVersion());
        if (updated == 0) {
            throw new BusinessException("该时间段号源已满，请选择其他时间段");
        }
        
        // 创建预约记录
        Appointment appointment = new Appointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setPatientId(patientId);
        appointment.setDoctorId(dto.getDoctorId());
        appointment.setDepartmentId(doctor.getDepartmentId());
        appointment.setTimeSlotId(dto.getTimeSlotId());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setPeriod(timeSlot.getPeriod());
        appointment.setPatientName(dto.getPatientName());
        appointment.setPatientPhone(dto.getPatientPhone());
        appointment.setSymptomDesc(dto.getSymptomDesc());
        appointment.setMedicalItemId(dto.getMedicalItemId());
        appointment.setStatus(AppointmentStatus.PENDING);
        
        appointment = appointmentRepository.save(appointment);
        
        // 发送预约成功通知
        try {
            String dateStr = schedule.getScheduleDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
            String timeStr = timeSlot.getPeriod().getDisplayName();
            notificationService.sendAppointmentSuccessNotification(
                    userId, 
                    appointment.getAppointmentNo(), 
                    doctor.getName(), 
                    dateStr, 
                    timeStr
            );
            log.info("预约成功通知已发送 - userId: {}", userId);
        } catch (Exception e) {
            log.warn("发送预约通知失败: {}", e.getMessage());
        }
        
        return convertToDetailDTO(appointment);
    }
    
    public AppointmentDetailDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        return convertToDetailDTO(appointment);
    }
    
    public AppointmentDetailDTO getAppointmentByNo(String appointmentNo) {
        Appointment appointment = appointmentRepository.findByAppointmentNo(appointmentNo)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        return convertToDetailDTO(appointment);
    }
    
    public Page<AppointmentDetailDTO> getMyAppointments(String userId, String status, int page, int size) {
        // 通过 userId 查找患者信息
        Patient patient = patientRepository.findByUserId(userId).orElse(null);
        if (patient == null) {
            return Page.empty(PageRequest.of(page, size));
        }
        
        String patientId = patient.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments;
        
        if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);
                appointments = appointmentRepository.findByPatientIdAndStatusOrderByCreatedAtDesc(
                        patientId, appointmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                appointments = appointmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
            }
        } else {
            appointments = appointmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        }
        
        return appointments.map(this::convertToDetailDTO);
    }
    
    @Transactional
    public void cancelAppointment(String appointmentId, String userId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        // 通过 userId 查找患者信息
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        
        if (!appointment.getPatientId().equals(patient.getId())) {
            throw new BusinessException("无权取消此预约");
        }
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessException("只能取消待就诊的预约");
        }
        
        // 检查是否在就诊前24小时
        LocalDateTime appointmentDateTime = appointment.getAppointmentDate().atStartOfDay();
        if (LocalDateTime.now().plusHours(24).isAfter(appointmentDateTime)) {
            throw new BusinessException("就诊前24小时内不可取消预约");
        }
        
        // 释放号源
        timeSlotRepository.incrementSlot(appointment.getTimeSlotId());
        
        // 更新预约状态
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(reason);
        appointmentRepository.save(appointment);
        
        // 发送取消通知
        try {
            notificationService.sendCancelNotification(userId, reason);
            log.info("预约取消通知已发送 - userId: {}", userId);
        } catch (Exception e) {
            log.warn("发送取消通知失败: {}", e.getMessage());
        }
    }
    
    @Transactional
    public AppointmentDetailDTO rescheduleAppointment(String appointmentId, String userId, String newTimeSlotId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        // 通过 userId 查找患者信息
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        
        if (!appointment.getPatientId().equals(patient.getId())) {
            throw new BusinessException("无权修改此预约");
        }
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessException("只能修改待就诊的预约");
        }
        
        // 检查是否在就诊前48小时
        LocalDateTime appointmentDateTime = appointment.getAppointmentDate().atStartOfDay();
        if (LocalDateTime.now().plusHours(48).isAfter(appointmentDateTime)) {
            throw new BusinessException("就诊前48小时内不可修改预约");
        }
        
        // 验证新时间段
        TimeSlot newTimeSlot = timeSlotRepository.findById(newTimeSlotId)
                .orElseThrow(() -> new BusinessException("新时间段不存在"));
        
        Schedule newSchedule = scheduleRepository.findById(newTimeSlot.getScheduleId())
                .orElseThrow(() -> new BusinessException("排班信息不存在"));
        
        // 检查新时间段是否属于同一医生
        if (!newSchedule.getDoctorId().equals(appointment.getDoctorId())) {
            throw new BusinessException("只能改期到同一医生的其他时间段");
        }
        
        // 使用乐观锁扣减新号源
        int updated = timeSlotRepository.decrementSlot(newTimeSlot.getId(), newTimeSlot.getVersion());
        if (updated == 0) {
            throw new BusinessException("新时间段号源已满，请选择其他时间段");
        }
        
        // 释放原号源
        timeSlotRepository.incrementSlot(appointment.getTimeSlotId());
        
        // 更新预约信息
        appointment.setTimeSlotId(newTimeSlotId);
        appointment.setAppointmentDate(newSchedule.getScheduleDate());
        appointment.setPeriod(newTimeSlot.getPeriod());
        appointment = appointmentRepository.save(appointment);
        
        return convertToDetailDTO(appointment);
    }
    
    public List<Appointment> findExpiredAppointments() {
        return appointmentRepository.findExpiredAppointments(LocalDate.now());
    }
    
    // 管理员删除预约（软删除，标记为已取消）
    @Transactional
    public void adminCancelAppointment(String appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("预约已取消");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("已完成的预约不能取消");
        }
        
        // 如果是待就诊状态，释放号源
        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            timeSlotRepository.incrementSlot(appointment.getTimeSlotId());
        }
        
        // 更新预约状态
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(reason != null ? reason : "管理员取消");
        appointmentRepository.save(appointment);
        
        log.info("管理员取消预约 - appointmentId: {}, reason: {}", appointmentId, reason);
    }
    
    // 管理员修改预约信息
    @Transactional
    public AppointmentDetailDTO adminUpdateAppointment(String appointmentId, AdminUpdateAppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("已完成的预约不能修改");
        }
        
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("已取消的预约不能修改");
        }
        
        // 如果修改了时间段，需要处理号源
        if (dto.getTimeSlotId() != null && !dto.getTimeSlotId().equals(appointment.getTimeSlotId())) {
            // 验证新时间段
            TimeSlot newTimeSlot = timeSlotRepository.findById(dto.getTimeSlotId())
                    .orElseThrow(() -> new BusinessException("新时间段不存在"));
            
            Schedule newSchedule = scheduleRepository.findById(newTimeSlot.getScheduleId())
                    .orElseThrow(() -> new BusinessException("排班信息不存在"));
            
            // 检查新时间段是否属于同一医生
            if (!newSchedule.getDoctorId().equals(appointment.getDoctorId())) {
                throw new BusinessException("只能改期到同一医生的其他时间段");
            }
            
            // 释放旧号源
            if (appointment.getStatus() == AppointmentStatus.PENDING) {
                timeSlotRepository.incrementSlot(appointment.getTimeSlotId());
            }
            
            // 使用乐观锁扣减新号源
            int updated = timeSlotRepository.decrementSlot(newTimeSlot.getId(), newTimeSlot.getVersion());
            if (updated == 0) {
                throw new BusinessException("该时间段号源已满");
            }
            
            appointment.setTimeSlotId(dto.getTimeSlotId());
            appointment.setAppointmentDate(newSchedule.getScheduleDate());
            appointment.setPeriod(newTimeSlot.getPeriod());
        }
        
        // 更新其他字段
        if (dto.getSymptomDesc() != null) {
            appointment.setSymptomDesc(dto.getSymptomDesc());
        }
        if (dto.getPatientName() != null) {
            appointment.setPatientName(dto.getPatientName());
        }
        if (dto.getPatientPhone() != null) {
            appointment.setPatientPhone(dto.getPatientPhone());
        }
        
        appointment = appointmentRepository.save(appointment);
        log.info("管理员修改预约 - appointmentId: {}", appointmentId);
        
        return convertToDetailDTO(appointment);
    }
    
    // 管理员查询所有预约
    public Page<AppointmentDetailDTO> getAllAppointments(String status, LocalDate appointmentDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments;
        
        if (status != null && !status.isEmpty() && appointmentDate != null) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);
                appointments = appointmentRepository.findByStatusAndAppointmentDateOrderByCreatedAtDesc(
                        appointmentStatus, appointmentDate, pageable);
            } catch (IllegalArgumentException e) {
                appointments = appointmentRepository.findByAppointmentDateOrderByCreatedAtDesc(appointmentDate, pageable);
            }
        } else if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);
                appointments = appointmentRepository.findByStatusOrderByCreatedAtDesc(appointmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                appointments = appointmentRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else if (appointmentDate != null) {
            appointments = appointmentRepository.findByAppointmentDateOrderByCreatedAtDesc(appointmentDate, pageable);
        } else {
            appointments = appointmentRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return appointments.map(this::convertToDetailDTO);
    }
    
    @Transactional
    public void markAsExpired(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            appointment.setStatus(AppointmentStatus.EXPIRED);
            appointmentRepository.save(appointment);
        }
    }
    
    private String generateAppointmentNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "GH" + dateStr + random;
    }
    
    private AppointmentDetailDTO convertToDetailDTO(Appointment appointment) {
        AppointmentDetailDTO dto = new AppointmentDetailDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentNo(appointment.getAppointmentNo());
        dto.setDoctorId(appointment.getDoctorId());
        dto.setDepartmentId(appointment.getDepartmentId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setPeriod(appointment.getPeriod().name());
        dto.setPeriodName(appointment.getPeriod().getDisplayName());
        dto.setPatientName(appointment.getPatientName());
        dto.setPatientPhone(appointment.getPatientPhone());
        dto.setSymptomDesc(appointment.getSymptomDesc());
        dto.setStatus(appointment.getStatus().name());
        dto.setStatusName(appointment.getStatus().getDisplayName());
        dto.setCreatedAt(appointment.getCreatedAt());
        
        // 获取医生信息
        doctorRepository.findById(appointment.getDoctorId()).ifPresent(doctor -> {
            dto.setDoctorName(doctor.getName());
            dto.setDoctorTitle(doctor.getTitle().name());
        });
        
        // 获取科室信息
        departmentRepository.findById(appointment.getDepartmentId()).ifPresent(dept -> {
            dto.setDepartmentName(dept.getName());
            dto.setDepartmentLocation(dept.getLocation());
        });
        
        // 获取时间段信息
        timeSlotRepository.findById(appointment.getTimeSlotId()).ifPresent(slot -> {
            dto.setTimeRange(slot.getStartTime() + " - " + slot.getEndTime());
        });
        
        // 获取检查项目信息
        if (appointment.getMedicalItemId() != null) {
            medicalItemRepository.findById(appointment.getMedicalItemId()).ifPresent(item -> {
                dto.setMedicalItemId(item.getId());
                dto.setMedicalItemName(item.getName());
                dto.setMedicalItemPrice(item.getPrice());
            });
        }
        
        return dto;
    }
}
