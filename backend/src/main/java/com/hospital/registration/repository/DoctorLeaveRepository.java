package com.hospital.registration.repository;

import com.hospital.registration.entity.DoctorLeave;
import com.hospital.registration.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, String> {
    
    Page<DoctorLeave> findByDoctorIdOrderByCreatedAtDesc(String doctorId, Pageable pageable);
    
    Page<DoctorLeave> findByStatusOrderByCreatedAtDesc(LeaveStatus status, Pageable pageable);
    
    @Query("SELECT dl FROM DoctorLeave dl WHERE dl.doctorId = :doctorId AND dl.status = 'APPROVED' " +
           "AND :date BETWEEN dl.startDate AND dl.endDate")
    List<DoctorLeave> findApprovedLeaveByDoctorAndDate(
            @Param("doctorId") String doctorId, 
            @Param("date") LocalDate date);
    
    @Query("SELECT dl FROM DoctorLeave dl WHERE dl.doctorId = :doctorId AND dl.status = 'PENDING' " +
           "AND ((dl.startDate <= :endDate AND dl.endDate >= :startDate))")
    List<DoctorLeave> findOverlappingPendingLeaves(
            @Param("doctorId") String doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
