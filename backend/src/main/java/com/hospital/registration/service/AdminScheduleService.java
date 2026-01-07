package com.hospital.registration.service;

import com.hospital.registration.dto.AdminScheduleDTO;
import com.hospital.registration.dto.CreateScheduleDTO;
import com.hospital.registration.dto.TimeSlotDTO;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Schedule;
import com.hospital.registration.entity.TimeSlot;
import com.hospital.registration.enums.TimePeriod;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AppointmentRepository;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.ScheduleRepository;
import com.hospital.registration.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public List<Schedule> batchCreateSchedules(CreateScheduleDTO dto) {
        log.info("开始批量创建排班: doctorId={}, startDate={}, endDate={}, weekdays={}, timeSlots={}", 
                dto.getDoctorId(), dto.getStartDate(), dto.getEndDate(), dto.getWeekdays(), dto.getTimeSlots());
        
        // 验证医生存在
        if (!doctorRepository.existsById(dto.getDoctorId())) {
            log.error("医生不存在: doctorId={}", dto.getDoctorId());
            throw new BusinessException("医生不存在");
        }
        
        // 验证时间段配置
        if (dto.getTimeSlots() == null || dto.getTimeSlots().isEmpty()) {
            log.error("时间段配置为空");
            throw new BusinessException("至少需要配置一个时间段");
        }
        
        List<Schedule> schedules = new ArrayList<>();
        LocalDate current = dto.getStartDate();
        int createdCount = 0;
        
        while (!current.isAfter(dto.getEndDate())) {
            log.info("处理日期: current={}, endDate={}", current, dto.getEndDate());
            
            // 根据weekdays过滤日期
            if (dto.getWeekdays() != null && !dto.getWeekdays().isEmpty()) {
                // Java的DayOfWeek: 1=周一, 2=周二, ..., 7=周日
                // 前端传的weekdays: 0=周日, 1=周一, ..., 6=周六
                int javaDayOfWeek = current.getDayOfWeek().getValue(); // 1-7
                // 转换为前端格式：周日(7) -> 0, 周一(1) -> 1, ..., 周六(6) -> 6
                int frontendDayOfWeek = (javaDayOfWeek == 7) ? 0 : javaDayOfWeek;
                
                // 确保weekdays中的所有值都是Integer类型
                boolean contains = false;
                for (Integer weekday : dto.getWeekdays()) {
                    if (weekday != null && weekday.intValue() == frontendDayOfWeek) {
                        contains = true;
                        break;
                    }
                }
                
                log.info("日期过滤: current={}, javaDayOfWeek={}, frontendDayOfWeek={}, weekdays={}, contains={}", 
                        current, javaDayOfWeek, frontendDayOfWeek, dto.getWeekdays(), contains);
                
                if (!contains) {
                    log.warn("跳过日期 {} (星期{}, 不在工作日列表中 {})", current, frontendDayOfWeek, dto.getWeekdays());
                    current = current.plusDays(1);
                    continue;
                }
            }
            
            log.info("创建排班: doctorId={}, date={}", dto.getDoctorId(), current);
            try {
                Schedule schedule = createScheduleForDate(dto.getDoctorId(), current, dto);
                schedules.add(schedule);
                createdCount++;
                log.info("排班创建成功: scheduleId={}, date={}", schedule.getId(), current);
            } catch (Exception e) {
                log.error("创建排班失败: date={}, error={}", current, e.getMessage(), e);
                throw e;
            }
            current = current.plusDays(1);
        }
        
        log.info("批量创建排班完成: 共创建 {} 个排班", createdCount);
        return schedules;
    }
    
    @Transactional
    public Schedule createScheduleForDate(String doctorId, LocalDate date, CreateScheduleDTO dto) {
        log.info("创建排班: doctorId={}, date={}", doctorId, date);
        
        // 检查是否已存在排班
        Schedule schedule = scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date)
                .orElse(null);
        
        boolean isNewSchedule = (schedule == null);
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setId(UUID.randomUUID().toString());
            schedule.setDoctorId(doctorId);
            schedule.setScheduleDate(date);
            log.info("创建新排班: scheduleId={}", schedule.getId());
        } else {
            log.info("更新已存在排班: scheduleId={}", schedule.getId());
        }
        
        schedule.setIsWorking(true);
        schedule = scheduleRepository.save(schedule);
        log.info("排班已保存: scheduleId={}, isNew={}", schedule.getId(), isNewSchedule);
        
        // 创建时间段 - 优先使用timeSlots配置
        int timeSlotCount = 0;
        if (dto.getTimeSlots() != null && !dto.getTimeSlots().isEmpty()) {
            log.info("使用timeSlots配置创建时间段，共 {} 个", dto.getTimeSlots().size());
            for (CreateScheduleDTO.TimeSlotConfig config : dto.getTimeSlots()) {
                boolean created = createTimeSlotFromConfig(schedule.getId(), config);
                if (created) {
                    timeSlotCount++;
                }
            }
        } else if (dto.getPeriods() != null && !dto.getPeriods().isEmpty()) {
            // 兼容旧版本
            log.info("使用periods配置创建时间段，共 {} 个", dto.getPeriods().size());
            for (String period : dto.getPeriods()) {
                boolean created = createTimeSlotIfNotExists(schedule.getId(), TimePeriod.valueOf(period), dto);
                if (created) {
                    timeSlotCount++;
                }
            }
        } else {
            // 默认创建上午和下午
            log.info("使用默认配置创建时间段");
            boolean morningCreated = createTimeSlotIfNotExists(schedule.getId(), TimePeriod.MORNING, dto);
            boolean afternoonCreated = createTimeSlotIfNotExists(schedule.getId(), TimePeriod.AFTERNOON, dto);
            if (morningCreated) timeSlotCount++;
            if (afternoonCreated) timeSlotCount++;
        }
        
        log.info("排班创建完成: scheduleId={}, 创建了 {} 个时间段", schedule.getId(), timeSlotCount);
        return schedule;
    }
    
    private boolean createTimeSlotFromConfig(String scheduleId, CreateScheduleDTO.TimeSlotConfig config) {
        try {
            TimePeriod period = TimePeriod.valueOf(config.getPeriod());
            
            // 检查是否已存在
            if (timeSlotRepository.findByScheduleIdAndPeriod(scheduleId, period).isPresent()) {
                log.debug("时间段已存在，跳过: scheduleId={}, period={}", scheduleId, period);
                return false; // 已存在则跳过
            }
            
            TimeSlot slot = new TimeSlot();
            slot.setId(UUID.randomUUID().toString());
            slot.setScheduleId(scheduleId);
            slot.setPeriod(period);
            
            // 解析时间字符串 (格式: HH:mm:ss 或 HH:mm)
            String startTimeStr = config.getStartTime();
            String endTimeStr = config.getEndTime();
            if (startTimeStr != null && startTimeStr.length() > 5) {
                startTimeStr = startTimeStr.substring(0, 5);
            }
            if (endTimeStr != null && endTimeStr.length() > 5) {
                endTimeStr = endTimeStr.substring(0, 5);
            }
            
            if (startTimeStr == null || endTimeStr == null) {
                log.error("时间段配置错误: startTime={}, endTime={}", config.getStartTime(), config.getEndTime());
                throw new BusinessException("时间段配置错误：开始时间和结束时间不能为空");
            }
            
            String[] startParts = startTimeStr.split(":");
            String[] endParts = endTimeStr.split(":");
            
            if (startParts.length < 2 || endParts.length < 2) {
                log.error("时间段格式错误: startTime={}, endTime={}", startTimeStr, endTimeStr);
                throw new BusinessException("时间段格式错误");
            }
            
            slot.setStartTime(LocalTime.of(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1])));
            slot.setEndTime(LocalTime.of(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1])));
            slot.setTotalSlots(config.getTotalSlots() != null ? config.getTotalSlots() : 20);
            slot.setRemainingSlots(slot.getTotalSlots());
            
            TimeSlot saved = timeSlotRepository.save(slot);
            log.info("时间段创建成功: timeSlotId={}, scheduleId={}, period={}, slots={}", 
                    saved.getId(), scheduleId, period, saved.getTotalSlots());
            return true;
        } catch (Exception e) {
            log.error("创建时间段失败: scheduleId={}, period={}, error={}", 
                    scheduleId, config.getPeriod(), e.getMessage(), e);
            throw new BusinessException("创建时间段失败: " + e.getMessage());
        }
    }
    
    private boolean createTimeSlotIfNotExists(String scheduleId, TimePeriod period, CreateScheduleDTO dto) {
        if (timeSlotRepository.findByScheduleIdAndPeriod(scheduleId, period).isPresent()) {
            log.debug("时间段已存在，跳过: scheduleId={}, period={}", scheduleId, period);
            return false; // 已存在则跳过
        }
        
        try {
            TimeSlot slot = new TimeSlot();
            slot.setId(UUID.randomUUID().toString());
            slot.setScheduleId(scheduleId);
            slot.setPeriod(period);
            
            switch (period) {
                case MORNING -> {
                    slot.setStartTime(LocalTime.of(8, 0));
                    slot.setEndTime(LocalTime.of(12, 0));
                    Integer morningSlots = dto.getMorningSlots() != null ? dto.getMorningSlots() : 20;
                    slot.setTotalSlots(morningSlots);
                    slot.setRemainingSlots(morningSlots);
                }
                case AFTERNOON -> {
                    slot.setStartTime(LocalTime.of(14, 0));
                    slot.setEndTime(LocalTime.of(17, 30));
                    Integer afternoonSlots = dto.getAfternoonSlots() != null ? dto.getAfternoonSlots() : 15;
                    slot.setTotalSlots(afternoonSlots);
                    slot.setRemainingSlots(afternoonSlots);
                }
                case EVENING -> {
                    slot.setStartTime(LocalTime.of(18, 0));
                    slot.setEndTime(LocalTime.of(21, 0));
                    Integer eveningSlots = dto.getEveningSlots() != null ? dto.getEveningSlots() : 10;
                    slot.setTotalSlots(eveningSlots);
                    slot.setRemainingSlots(eveningSlots);
                }
            }
            
            TimeSlot saved = timeSlotRepository.save(slot);
            log.info("时间段创建成功: timeSlotId={}, scheduleId={}, period={}, slots={}", 
                    saved.getId(), scheduleId, period, saved.getTotalSlots());
            return true;
        } catch (Exception e) {
            log.error("创建时间段失败: scheduleId={}, period={}, error={}", 
                    scheduleId, period, e.getMessage(), e);
            throw new BusinessException("创建时间段失败: " + e.getMessage());
        }
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
    
    public Page<AdminScheduleDTO> getSchedules(String doctorId, String departmentId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<Schedule> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scheduleDate"), startDate));
            }
            
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scheduleDate"), endDate));
            }
            
            // 如果指定了科室，需要关联医生表
            if (departmentId != null && !departmentId.isEmpty()) {
                // 先获取该科室的所有医生ID
                List<Doctor> doctors = doctorRepository.findByDepartmentId(departmentId);
                List<String> doctorIds = doctors.stream().map(Doctor::getId).collect(Collectors.toList());
                if (doctorIds.isEmpty()) {
                    // 如果没有医生，返回空结果
                    predicates.add(cb.disjunction());
                } else {
                    predicates.add(root.get("doctorId").in(doctorIds));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Schedule> schedules = scheduleRepository.findAll(spec, pageable);
        
        // 获取所有医生信息
        List<String> doctorIds = schedules.getContent().stream()
                .map(Schedule::getDoctorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        final Map<String, Doctor> doctorMap;
        if (!doctorIds.isEmpty()) {
            doctorMap = doctorRepository.findAllById(doctorIds).stream()
                    .collect(Collectors.toMap(Doctor::getId, d -> d));
        } else {
            doctorMap = new java.util.HashMap<>();
        }
        
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        return schedules.map(schedule -> {
            AdminScheduleDTO dto = new AdminScheduleDTO();
            dto.setId(schedule.getId() != null ? schedule.getId() : "");
            dto.setDoctorId(schedule.getDoctorId() != null ? schedule.getDoctorId() : "");
            if (schedule.getScheduleDate() != null) {
                dto.setScheduleDate(schedule.getScheduleDate().format(dateFormatter));
            } else {
                dto.setScheduleDate("");
            }
            dto.setIsWorking(schedule.getIsWorking() != null ? schedule.getIsWorking() : false);
            
            Doctor doctor = doctorMap.get(schedule.getDoctorId());
            if (doctor != null) {
                dto.setDoctorName(doctor.getName() != null ? doctor.getName() : "");
                // 获取科室名称
                if (doctor.getDepartmentId() != null) {
                    try {
                        departmentRepository.findById(doctor.getDepartmentId())
                                .ifPresent(dept -> dto.setDepartmentName(dept.getName() != null ? dept.getName() : ""));
                    } catch (Exception e) {
                        // 忽略科室查询错误
                        dto.setDepartmentName("");
                    }
                } else {
                    dto.setDepartmentName("");
                }
            } else {
                dto.setDoctorName("");
                dto.setDepartmentName("");
            }
            
            // 获取时间段
            List<TimeSlot> slots = new ArrayList<>();
            if (schedule.getId() != null) {
                try {
                    slots = timeSlotRepository.findByScheduleIdOrderByPeriodAsc(schedule.getId());
                } catch (Exception e) {
                    // 忽略查询错误
                    slots = new ArrayList<>();
                }
            }
            List<TimeSlotDTO> slotDTOs = new ArrayList<>();
            if (slots != null && !slots.isEmpty()) {
                slotDTOs = slots.stream().map(slot -> {
                    TimeSlotDTO slotDTO = new TimeSlotDTO();
                    slotDTO.setId(slot.getId() != null ? slot.getId() : "");
                    slotDTO.setPeriod(slot.getPeriod() != null ? slot.getPeriod().name() : "");
                    slotDTO.setPeriodName(slot.getPeriod() != null ? slot.getPeriod().getDisplayName() : "");
                    if (slot.getStartTime() != null && slot.getEndTime() != null) {
                        slotDTO.setTimeRange(slot.getStartTime().format(timeFormatter) + " - " + slot.getEndTime().format(timeFormatter));
                    } else {
                        slotDTO.setTimeRange("");
                    }
                    slotDTO.setTotalSlots(slot.getTotalSlots() != null ? slot.getTotalSlots() : 0);
                    slotDTO.setRemainingSlots(slot.getRemainingSlots() != null ? slot.getRemainingSlots() : 0);
                    slotDTO.setAvailable(slot.getRemainingSlots() != null && slot.getRemainingSlots() > 0);
                    return slotDTO;
                }).collect(Collectors.toList());
            }
            dto.setTimeSlots(slotDTOs);
            
            // 计算总号源和剩余号源
            int totalSlots = slots != null ? slots.stream()
                    .filter(s -> s.getTotalSlots() != null)
                    .mapToInt(TimeSlot::getTotalSlots).sum() : 0;
            int remainingSlots = slots != null ? slots.stream()
                    .filter(s -> s.getRemainingSlots() != null)
                    .mapToInt(TimeSlot::getRemainingSlots).sum() : 0;
            dto.setTotalSlots(totalSlots);
            dto.setRemainingSlots(remainingSlots);
            
            // 获取预约数量
            if (doctor != null && doctor.getId() != null && schedule.getScheduleDate() != null) {
                try {
                    int appointmentCount = (int) appointmentRepository.countByDoctorIdAndAppointmentDate(
                            doctor.getId(), schedule.getScheduleDate());
                    dto.setAppointmentCount(appointmentCount);
                } catch (Exception e) {
                    dto.setAppointmentCount(0);
                }
            } else {
                dto.setAppointmentCount(0);
            }
            
            return dto;
        });
    }
}
