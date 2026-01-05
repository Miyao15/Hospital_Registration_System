package com.hospital.registration.service;

import com.hospital.registration.dto.AvailableDateDTO;
import com.hospital.registration.dto.DoctorScheduleDTO;
import com.hospital.registration.dto.TimeSlotDTO;
import com.hospital.registration.entity.Appointment;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Schedule;
import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.TimePeriod;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AppointmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.ScheduleRepository;
import com.hospital.registration.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    
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
    
    /**
     * 医生端 - 获取月排班信息
     */
    public List<DoctorScheduleDTO> getDoctorSchedulesByUserId(String userId, int year, int month) {
        log.info("获取医生排班信息 - userId: {}, year: {}, month: {}", userId, year, month);
        
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndScheduleDateBetweenOrderByScheduleDateAsc(
                doctor.getId(), startDate, endDate);
        
        List<DoctorScheduleDTO> result = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Schedule schedule : schedules) {
            DoctorScheduleDTO dto = new DoctorScheduleDTO();
            dto.setId(schedule.getId());
            dto.setScheduleDate(schedule.getScheduleDate().format(dateFormatter));
            dto.setIsWorking(schedule.getIsWorking());
            
            if (schedule.getIsWorking()) {
                List<TimeSlot> slots = timeSlotRepository.findByScheduleIdOrderByPeriodAsc(schedule.getId());
                
                for (TimeSlot slot : slots) {
                    if (slot.getPeriod() == TimePeriod.MORNING) {
                        dto.setHasMorning(true);
                        dto.setMorningTime(slot.getStartTime().format(timeFormatter) + " - " + slot.getEndTime().format(timeFormatter));
                        dto.setMorningSlots(slot.getRemainingSlots());
                        dto.setMorningTotalSlots(slot.getTotalSlots());
                        dto.setMorningBooked(slot.getTotalSlots() - slot.getRemainingSlots());
                    } else if (slot.getPeriod() == TimePeriod.AFTERNOON) {
                        dto.setHasAfternoon(true);
                        dto.setAfternoonTime(slot.getStartTime().format(timeFormatter) + " - " + slot.getEndTime().format(timeFormatter));
                        dto.setAfternoonSlots(slot.getRemainingSlots());
                        dto.setAfternoonTotalSlots(slot.getTotalSlots());
                        dto.setAfternoonBooked(slot.getTotalSlots() - slot.getRemainingSlots());
                    }
                }
                
                // 获取当天预约数量
                int appointmentCount = (int) appointmentRepository.countByDoctorIdAndAppointmentDate(
                        doctor.getId(), schedule.getScheduleDate());
                dto.setAppointmentCount(appointmentCount);
            }
            
            result.add(dto);
        }
        
        return result;
    }

    /**
     * 医生端 - 新增排班
     */
    @Transactional
    public java.util.Map<String, Object> createScheduleByDoctor(String userId, com.hospital.registration.dto.CreateScheduleDTO dto) {
        log.info("医生新增排班 - userId: {}, dto: {}", userId, dto);
        
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : startDate;
        
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("不能为过去的日期创建排班");
        }
        
        // 根据排班类型设置默认值
        int morningSlots = 20;
        int afternoonSlots = 15;
        LocalTime morningStart = LocalTime.of(8, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);
        LocalTime afternoonStart = LocalTime.of(14, 0);
        LocalTime afternoonEnd = LocalTime.of(17, 30);
        
        if ("TYPE1".equals(dto.getScheduleType())) {
            morningSlots = 10;
            afternoonSlots = 5;
        } else if ("TYPE2".equals(dto.getScheduleType())) {
            morningSlots = 20;
            afternoonSlots = 15;
        }
        
        // 使用自定义值覆盖
        if (dto.getMorningSlots() != null) morningSlots = dto.getMorningSlots();
        if (dto.getAfternoonSlots() != null) afternoonSlots = dto.getAfternoonSlots();
        if (dto.getMorningStartTime() != null) morningStart = dto.getMorningStartTime();
        if (dto.getMorningEndTime() != null) morningEnd = dto.getMorningEndTime();
        if (dto.getAfternoonStartTime() != null) afternoonStart = dto.getAfternoonStartTime();
        if (dto.getAfternoonEndTime() != null) afternoonEnd = dto.getAfternoonEndTime();
        
        int createdCount = 0;
        int skippedCount = 0;
        List<String> skippedDates = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 检查是否已存在排班
            if (scheduleRepository.findByDoctorIdAndScheduleDate(doctor.getId(), date).isPresent()) {
                skippedCount++;
                skippedDates.add(date.toString());
                continue;
            }
            
            // 创建排班
            Schedule schedule = new Schedule();
            schedule.setDoctorId(doctor.getId());
            schedule.setScheduleDate(date);
            schedule.setIsWorking(true);
            schedule = scheduleRepository.save(schedule);
            
            // 创建时间段
            if (Boolean.TRUE.equals(dto.getHasMorning())) {
                TimeSlot morning = new TimeSlot();
                morning.setScheduleId(schedule.getId());
                morning.setPeriod(TimePeriod.MORNING);
                morning.setStartTime(morningStart);
                morning.setEndTime(morningEnd);
                morning.setTotalSlots(morningSlots);
                morning.setRemainingSlots(morningSlots);
                timeSlotRepository.save(morning);
            }
            
            if (Boolean.TRUE.equals(dto.getHasAfternoon())) {
                TimeSlot afternoon = new TimeSlot();
                afternoon.setScheduleId(schedule.getId());
                afternoon.setPeriod(TimePeriod.AFTERNOON);
                afternoon.setStartTime(afternoonStart);
                afternoon.setEndTime(afternoonEnd);
                afternoon.setTotalSlots(afternoonSlots);
                afternoon.setRemainingSlots(afternoonSlots);
                timeSlotRepository.save(afternoon);
            }
            
            createdCount++;
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("createdCount", createdCount);
        result.put("skippedCount", skippedCount);
        result.put("skippedDates", skippedDates);
        result.put("message", String.format("成功创建 %d 天排班，跳过 %d 天（已有排班）", createdCount, skippedCount));
        
        return result;
    }
}
