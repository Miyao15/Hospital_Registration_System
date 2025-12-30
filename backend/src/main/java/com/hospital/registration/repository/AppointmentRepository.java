package com.hospital.registration.repository;

import com.hospital.registration.entity.Appointment;
import com.hospital.registration.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    
    Optional<Appointment> findByAppointmentNo(String appointmentNo);
    
    Page<Appointment> findByPatientIdOrderByCreatedAtDesc(String patientId, Pageable pageable);
    
    Page<Appointment> findByPatientIdAndStatusOrderByCreatedAtDesc(String patientId, AppointmentStatus status, Pageable pageable);
    
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByPeriodAsc(String doctorId, LocalDate appointmentDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patientId = :patientId AND a.doctorId = :doctorId AND a.appointmentDate = :date AND a.status = 'PENDING'")
    int countExistingAppointment(
            @Param("patientId") String patientId,
            @Param("doctorId") String doctorId,
            @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate < :date AND a.status = 'PENDING'")
    List<Appointment> findExpiredAppointments(@Param("date") LocalDate date);
    
    List<Appointment> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate BETWEEN :startDate AND :endDate AND a.status = 'PENDING'")
    List<Appointment> findPendingByDoctorAndDateRange(
            @Param("doctorId") String doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // 统计指定医生指定日期的预约数量
    int countByDoctorIdAndAppointmentDate(String doctorId, LocalDate appointmentDate);
    
    // 管理员查询所有预约（支持筛选）
    Page<Appointment> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Appointment> findByStatusOrderByCreatedAtDesc(AppointmentStatus status, Pageable pageable);
    
    Page<Appointment> findByAppointmentDateOrderByCreatedAtDesc(LocalDate appointmentDate, Pageable pageable);
    
    Page<Appointment> findByStatusAndAppointmentDateOrderByCreatedAtDesc(AppointmentStatus status, LocalDate appointmentDate, Pageable pageable);
}
