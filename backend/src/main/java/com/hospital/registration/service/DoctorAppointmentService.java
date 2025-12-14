package com.hospital.registration.service;

import com.hospital.registration.dto.DoctorAppointmentDTO;
import com.hospital.registration.entity.Appointment;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AppointmentRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorAppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    public List<DoctorAppointmentDTO> getTodayAppointments(String doctorId) {
        return getAppointmentsByDate(doctorId, LocalDate.now());
    }
    
    public List<DoctorAppointmentDTO> getAppointmentsByDate(String doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByPeriodAsc(doctorId, date);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markCheckedIn(String appointmentId, String doctorId) {
        Appointment appointment = getAndValidateAppointment(appointmentId, doctorId);
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessException("只能标记待就诊的预约为已到诊");
        }
        
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void markCompleted(String appointmentId, String doctorId) {
        Appointment appointment = getAndValidateAppointment(appointmentId, doctorId);
        
        if (appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
            throw new BusinessException("只能标记已到诊的预约为已完成");
        }
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void markNoShow(String appointmentId, String doctorId) {
        Appointment appointment = getAndValidateAppointment(appointmentId, doctorId);
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessException("只能标记待就诊的预约为未到诊");
        }
        
        appointment.setStatus(AppointmentStatus.EXPIRED);
        appointmentRepository.save(appointment);
        
        // TODO: 记录患者爽约次数
    }
    
    private Appointment getAndValidateAppointment(String appointmentId, String doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        return appointment;
    }
    
    private DoctorAppointmentDTO convertToDTO(Appointment appointment) {
        DoctorAppointmentDTO dto = new DoctorAppointmentDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentNo(appointment.getAppointmentNo());
        dto.setPatientId(appointment.getPatientId());
        dto.setPatientName(appointment.getPatientName());
        dto.setPatientPhone(appointment.getPatientPhone());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setPeriod(appointment.getPeriod().name());
        dto.setPeriodName(appointment.getPeriod().getDisplayName());
        dto.setSymptomDesc(appointment.getSymptomDesc());
        dto.setStatus(appointment.getStatus().name());
        dto.setStatusName(appointment.getStatus().getDisplayName());
        dto.setCreatedAt(appointment.getCreatedAt());
        
        // 获取患者详细信息
        patientRepository.findById(appointment.getPatientId()).ifPresent(patient -> {
            dto.setPatientGender(patient.getGender() != null ? patient.getGender().name() : null);
            if (patient.getBirthDate() != null) {
                dto.setPatientAge(Period.between(patient.getBirthDate(), LocalDate.now()).getYears());
            }
        });
        
        // 获取时间段信息
        timeSlotRepository.findById(appointment.getTimeSlotId()).ifPresent(slot -> {
            dto.setTimeRange(slot.getStartTime() + " - " + slot.getEndTime());
        });
        
        return dto;
    }
}
