package com.hospital.registration.service;

import com.hospital.registration.dto.AvailableDateDTO;
import com.hospital.registration.dto.TimeSlotDTO;
import com.hospital.registration.entity.Schedule;
import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.TimePeriod;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.ScheduleRepository;
import com.hospital.registration.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    public List<AvailableDateDTO> getAvailableDates(String doctorId, int days) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(days - 1);
        
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndScheduleDateBetweenOrderByScheduleDateAsc(
                doctorId, startDate, endDate);
        
        List<AvailableDateDTO> result = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            AvailableDateDTO dto = new AvailableDateDTO();
            dto.setDate(date);
            dto.setDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CHINESE));
            
            LocalDate finalDate = date;
            Schedule schedule = schedules.stream()
                    .filter(s -> s.getScheduleDate().equals(finalDate))
                    .findFirst()
                    .orElse(null);
            
            if (schedule != null && schedule.getIsWorking()) {
                Integer remaining = timeSlotRepository.getTotalRemainingSlotsByScheduleId(schedule.getId());
                dto.setAvailable(remaining != null && remaining > 0);
                dto.setRemainingSlots(remaining != null ? remaining : 0);
                
                List<TimeSlot> slots = timeSlotRepository.findByScheduleIdOrderByPeriodAsc(schedule.getId());
                dto.setTotalSlots(slots.stream().mapToInt(TimeSlot::getTotalSlots).sum());
            } else {
                // 周末默认不可预约，除非有排班
                boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || 
                                   date.getDayOfWeek() == DayOfWeek.SUNDAY;
                dto.setAvailable(false);
                dto.setTotalSlots(0);
                dto.setRemainingSlots(0);
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    public List<TimeSlotDTO> getTimeSlots(String doctorId, LocalDate date) {
        Schedule schedule = scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date)
                .orElseThrow(() -> new BusinessException("该日期暂无排班"));
        
        if (!schedule.getIsWorking()) {
            throw new BusinessException("该日期医生休息");
        }
        
        List<TimeSlot> slots = timeSlotRepository.findByScheduleIdOrderByPeriodAsc(schedule.getId());
        
        return slots.stream().map(this::convertToDTO).toList();
    }
    
    @Transactional
    public Schedule createOrUpdateSchedule(String doctorId, LocalDate date, boolean isWorking) {
        Schedule schedule = scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date)
                .orElse(new Schedule());
        
        if (schedule.getId() == null) {
            schedule.setId(java.util.UUID.randomUUID().toString());
            schedule.setDoctorId(doctorId);
            schedule.setScheduleDate(date);
        }
        schedule.setIsWorking(isWorking);
        
        schedule = scheduleRepository.save(schedule);
        
        // 如果是工作日且没有时间段，创建默认时间段
        if (isWorking) {
            List<TimeSlot> existingSlots = timeSlotRepository.findByScheduleIdOrderByPeriodAsc(schedule.getId());
            if (existingSlots.isEmpty()) {
                createDefaultTimeSlots(schedule.getId());
            }
        }
        
        return schedule;
    }
    
    private void createDefaultTimeSlots(String scheduleId) {
        // 上午 8:00-12:00
        TimeSlot morning = new TimeSlot();
        morning.setScheduleId(scheduleId);
        morning.setPeriod(TimePeriod.MORNING);
        morning.setStartTime(LocalTime.of(8, 0));
        morning.setEndTime(LocalTime.of(12, 0));
        morning.setTotalSlots(20);
        morning.setRemainingSlots(20);
        timeSlotRepository.save(morning);
        
        // 下午 14:00-17:30
        TimeSlot afternoon = new TimeSlot();
        afternoon.setScheduleId(scheduleId);
        afternoon.setPeriod(TimePeriod.AFTERNOON);
        afternoon.setStartTime(LocalTime.of(14, 0));
        afternoon.setEndTime(LocalTime.of(17, 30));
        afternoon.setTotalSlots(15);
        afternoon.setRemainingSlots(15);
        timeSlotRepository.save(afternoon);
    }
    
    private TimeSlotDTO convertToDTO(TimeSlot slot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setId(slot.getId());
        dto.setPeriod(slot.getPeriod().name());
        dto.setPeriodName(slot.getPeriod().getDisplayName());
        dto.setTimeRange(slot.getStartTime() + " - " + slot.getEndTime());
        dto.setTotalSlots(slot.getTotalSlots());
        dto.setRemainingSlots(slot.getRemainingSlots());
        dto.setAvailable(slot.getRemainingSlots() > 0);
        return dto;
    }
}
