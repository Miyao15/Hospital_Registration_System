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
public class AdminScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public List<Schedule> batchCreateSchedules(CreateScheduleDTO dto) {
        // 验证医生存在
        if (!doctorRepository.existsById(dto.getDoctorId())) {
            throw new BusinessException("医生不存在");
        }
        
        // 验证时间段配置
        if (dto.getTimeSlots() == null || dto.getTimeSlots().isEmpty()) {
            throw new BusinessException("至少需要配置一个时间段");
        }
        
        List<Schedule> schedules = new ArrayList<>();
        LocalDate current = dto.getStartDate();
        
        while (!current.isAfter(dto.getEndDate())) {
            // 根据weekdays过滤日期
            if (dto.getWeekdays() != null && !dto.getWeekdays().isEmpty()) {
                // Java的DayOfWeek: 1=周一, 7=周日
                // 前端传的weekdays: 0=周日, 1=周一, ..., 6=周六
                int javaDayOfWeek = current.getDayOfWeek().getValue(); // 1-7
                int frontendDayOfWeek = javaDayOfWeek % 7; // 转换为0-6 (1->1, 2->2, ..., 6->6, 7->0)
                if (!dto.getWeekdays().contains(frontendDayOfWeek)) {
                    current = current.plusDays(1);
                    continue;
                }
            }
            
            Schedule schedule = createScheduleForDate(dto.getDoctorId(), current, dto);
            schedules.add(schedule);
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
        
        // 创建时间段 - 优先使用timeSlots配置
        if (dto.getTimeSlots() != null && !dto.getTimeSlots().isEmpty()) {
            for (CreateScheduleDTO.TimeSlotConfig config : dto.getTimeSlots()) {
                createTimeSlotFromConfig(schedule.getId(), config);
            }
        } else if (dto.getPeriods() != null && !dto.getPeriods().isEmpty()) {
            // 兼容旧版本
            for (String period : dto.getPeriods()) {
                createTimeSlotIfNotExists(schedule.getId(), TimePeriod.valueOf(period), dto);
            }
        } else {
            // 默认创建上午和下午
            createTimeSlotIfNotExists(schedule.getId(), TimePeriod.MORNING, dto);
            createTimeSlotIfNotExists(schedule.getId(), TimePeriod.AFTERNOON, dto);
        }
        
        return schedule;
    }
    
    private void createTimeSlotFromConfig(String scheduleId, CreateScheduleDTO.TimeSlotConfig config) {
        TimePeriod period = TimePeriod.valueOf(config.getPeriod());
        
        // 检查是否已存在
        if (timeSlotRepository.findByScheduleIdAndPeriod(scheduleId, period).isPresent()) {
            return; // 已存在则跳过
        }
        
        TimeSlot slot = new TimeSlot();
        slot.setId(UUID.randomUUID().toString());
        slot.setScheduleId(scheduleId);
        slot.setPeriod(period);
        
        // 解析时间字符串 (格式: HH:mm:ss 或 HH:mm)
        String startTimeStr = config.getStartTime();
        String endTimeStr = config.getEndTime();
        if (startTimeStr.length() > 5) {
            startTimeStr = startTimeStr.substring(0, 5);
        }
        if (endTimeStr.length() > 5) {
            endTimeStr = endTimeStr.substring(0, 5);
        }
        
        String[] startParts = startTimeStr.split(":");
        String[] endParts = endTimeStr.split(":");
        slot.setStartTime(LocalTime.of(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1])));
        slot.setEndTime(LocalTime.of(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1])));
        slot.setTotalSlots(config.getTotalSlots());
        slot.setRemainingSlots(config.getTotalSlots());
        
        timeSlotRepository.save(slot);
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
                    int appointmentCount = appointmentRepository.countByDoctorIdAndAppointmentDate(
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
