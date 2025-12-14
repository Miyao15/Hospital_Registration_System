package com.hospital.registration.service;

import com.hospital.registration.entity.Appointment;
import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.enums.NotificationType;
import com.hospital.registration.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTaskService {
    
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    
    /**
     * 每天凌晨1点执行：处理过期预约
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processExpiredAppointments() {
        log.info("开始处理过期预约...");
        List<Appointment> expiredList = appointmentRepository.findExpiredAppointments(LocalDate.now());
        
        for (Appointment apt : expiredList) {
            apt.setStatus(AppointmentStatus.EXPIRED);
            appointmentRepository.save(apt);
            log.info("预约 {} 已标记为过期", apt.getAppointmentNo());
        }
        log.info("过期预约处理完成，共处理 {} 条", expiredList.size());
    }
    
    /**
     * 每天早上8点执行：发送就诊提醒（提前1天）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendAppointmentReminders() {
        log.info("开始发送就诊提醒...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateAndStatus(tomorrow, AppointmentStatus.PENDING);
        
        for (Appointment apt : appointments) {
            notificationService.sendNotification(
                    apt.getPatientId(),
                    "就诊提醒",
                    String.format("您预约的 %s 就诊即将到来，请准时前往医院", apt.getAppointmentDate()),
                    NotificationType.APPOINTMENT_REMINDER,
                    apt.getId()
            );
        }
        log.info("就诊提醒发送完成，共发送 {} 条", appointments.size());
    }
    
    /**
     * 每小时执行：发送当天就诊提醒（提前1小时）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void sendHourlyReminders() {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateAndStatus(today, AppointmentStatus.PENDING);
        
        for (Appointment apt : appointments) {
            // 简化处理：当天的预约都发送提醒
            notificationService.sendNotification(
                    apt.getPatientId(),
                    "就诊提醒",
                    String.format("您今天有预约就诊，请注意时间安排"),
                    NotificationType.APPOINTMENT_REMINDER,
                    apt.getId()
            );
        }
    }
}
