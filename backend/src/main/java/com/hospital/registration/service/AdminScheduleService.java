package com.hospital.registration.service;

import com.hospital.registration.dto.CreateScheduleDTO;
import com.hospital.registration.entity.Schedule;
import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.TimePeriod;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.ScheduleRepository;
import com.hospital.registration.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    
    @Transactional
    public List<Schedule> batchCreateSchedules(CreateScheduleDTO dto) {
        // 验证医生存在
        if (!doctorRepository.existsById(dto.getDoctorId())) {
            throw new BusinessException("医生不存在");
        }
        
        List<Schedule> schedules = new ArrayList<>();
        LocalDate current = dto.getStartDate();
        
        while (!current.isAfter(dto.getEndDate())) {
            // 跳过周末（可配置）
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && 
                current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                
                Schedule schedule = createScheduleForDate(dto.getDoctorId(), current, dto);
                schedules.add(schedule);
            }
            current = current.plusDays(1);
        }
        
        return schedules;
    }
    
    @Transactional
    public Schedule createScheduleForDate(String doctorId, LocalDate date, CreateScheduleDTO dto) {
        // 检查是否已存在排班
        Schedule schedule = scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date)
                .orElse(null);
        
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setId(UUID.randomUUID().toString());
            schedule.setDoctorId(doctorId);
            schedule.setScheduleDate(date);
        }
        schedule.setIsWorking(true);
        schedule = scheduleRepository.save(schedule);
        
        // 创建时间段
        List<String> periods = dto.getPeriods();
        if (periods == null || periods.isEmpty()) {
            periods = List.of("MORNING", "AFTERNOON");
        }
        
        for (String period : periods) {
            createTimeSlotIfNotExists(schedule.getId(), TimePeriod.valueOf(period), dto);
        }
        
        return schedule;
    }
    
    private void createTimeSlotIfNotExists(String scheduleId, TimePeriod period, CreateScheduleDTO dto) {
        if (timeSlotRepository.findByScheduleIdAndPeriod(scheduleId, period).isPresent()) {
            return; // 已存在则跳过
        }
        
        TimeSlot slot = new TimeSlot();
        slot.setId(UUID.randomUUID().toString());
        slot.setScheduleId(scheduleId);
        slot.setPeriod(period);
        
        switch (period) {
            case MORNING -> {
                slot.setStartTime(LocalTime.of(8, 0));
                slot.setEndTime(LocalTime.of(12, 0));
                slot.setTotalSlots(dto.getMorningSlots());
                slot.setRemainingSlots(dto.getMorningSlots());
            }
            case AFTERNOON -> {
                slot.setStartTime(LocalTime.of(14, 0));
                slot.setEndTime(LocalTime.of(17, 30));
                slot.setTotalSlots(dto.getAfternoonSlots());
                slot.setRemainingSlots(dto.getAfternoonSlots());
            }
            case EVENING -> {
                slot.setStartTime(LocalTime.of(18, 0));
                slot.setEndTime(LocalTime.of(21, 0));
                slot.setTotalSlots(dto.getEveningSlots());
                slot.setRemainingSlots(dto.getEveningSlots());
            }
        }
        
        timeSlotRepository.save(slot);
    }
    
    @Transactional
    public void cancelSchedule(String doctorId, LocalDate date) {
        Schedule schedule = scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date)
                .orElseThrow(() -> new BusinessException("排班不存在"));
        
        schedule.setIsWorking(false);
        schedule.setRemark("管理员取消排班");
        scheduleRepository.save(schedule);
        
        // TODO: 通知已预约的患者
    }
    
    @Transactional
    public void updateSlotCount(String timeSlotId, int totalSlots) {
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new BusinessException("时间段不存在"));
        
        int used = slot.getTotalSlots() - slot.getRemainingSlots();
        if (totalSlots < used) {
            throw new BusinessException("新号源数量不能小于已预约数量");
        }
        
        slot.setTotalSlots(totalSlots);
        slot.setRemainingSlots(totalSlots - used);
        timeSlotRepository.save(slot);
    }
}
