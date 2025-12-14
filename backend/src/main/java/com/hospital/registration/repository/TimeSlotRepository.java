package com.hospital.registration.repository;

import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.TimePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
    
    List<TimeSlot> findByScheduleIdOrderByPeriodAsc(String scheduleId);
    
    Optional<TimeSlot> findByScheduleIdAndPeriod(String scheduleId, TimePeriod period);
    
    @Query("SELECT SUM(ts.remainingSlots) FROM TimeSlot ts WHERE ts.scheduleId = :scheduleId")
    Integer getTotalRemainingSlotsByScheduleId(@Param("scheduleId") String scheduleId);
    
    @Modifying
    @Query("UPDATE TimeSlot ts SET ts.remainingSlots = ts.remainingSlots - 1, ts.version = ts.version + 1 WHERE ts.id = :id AND ts.remainingSlots > 0 AND ts.version = :version")
    int decrementSlot(@Param("id") String id, @Param("version") Integer version);
    
    @Modifying
    @Query("UPDATE TimeSlot ts SET ts.remainingSlots = ts.remainingSlots + 1 WHERE ts.id = :id")
    int incrementSlot(@Param("id") String id);
}
