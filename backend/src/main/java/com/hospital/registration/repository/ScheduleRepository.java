package com.hospital.registration.repository;

import com.hospital.registration.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    
    Optional<Schedule> findByDoctorIdAndScheduleDate(String doctorId, LocalDate scheduleDate);
    
    @Query("SELECT s FROM Schedule s WHERE s.doctorId = :doctorId AND s.scheduleDate BETWEEN :startDate AND :endDate AND s.isWorking = true ORDER BY s.scheduleDate")
    List<Schedule> findAvailableSchedules(
            @Param("doctorId") String doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    List<Schedule> findByDoctorIdAndScheduleDateBetweenOrderByScheduleDateAsc(
            String doctorId, LocalDate startDate, LocalDate endDate);
}
