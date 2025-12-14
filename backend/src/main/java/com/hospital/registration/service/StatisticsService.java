package com.hospital.registration.service;

import com.hospital.registration.dto.StatisticsDTO;
import com.hospital.registration.enums.AppointmentStatus;
import com.hospital.registration.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final EntityManager entityManager;
    
    public StatisticsDTO getDashboardStatistics() {
        StatisticsDTO stats = new StatisticsDTO();
        LocalDate today = LocalDate.now();
        
        // 今日统计
        stats.setTodayAppointments(countAppointmentsByDate(today, null));
        stats.setTodayCheckedIn(countAppointmentsByDate(today, AppointmentStatus.CHECKED_IN));
        stats.setTodayCompleted(countAppointmentsByDate(today, AppointmentStatus.COMPLETED));
        stats.setTodayNoShow(countAppointmentsByDate(today, AppointmentStatus.EXPIRED));
        
        // 总体统计
        stats.setTotalPatients((int) patientRepository.count());
        stats.setTotalDoctors((int) doctorRepository.count());
        stats.setTotalAppointments((int) appointmentRepository.count());
        
        // 就诊率计算
        int totalToday = stats.getTodayAppointments();
        if (totalToday > 0) {
            stats.setCheckInRate((double) (stats.getTodayCheckedIn() + stats.getTodayCompleted()) / totalToday * 100);
            stats.setNoShowRate((double) stats.getTodayNoShow() / totalToday * 100);
        } else {
            stats.setCheckInRate(0.0);
            stats.setNoShowRate(0.0);
        }
        
        // 科室预约排行
        stats.setDepartmentRanking(getDepartmentRanking());
        
        // 医生工作量排行
        stats.setDoctorWorkload(getDoctorWorkload());
        
        return stats;
    }
    
    private int countAppointmentsByDate(LocalDate date, AppointmentStatus status) {
        String sql = "SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date";
        if (status != null) {
            sql += " AND a.status = :status";
        }
        
        Query query = entityManager.createQuery(sql);
        query.setParameter("date", date);
        if (status != null) {
            query.setParameter("status", status);
        }
        
        return ((Long) query.getSingleResult()).intValue();
    }
    
    private List<Map<String, Object>> getDepartmentRanking() {
        String sql = """
            SELECT a.departmentId, COUNT(a) as count 
            FROM Appointment a 
            WHERE a.appointmentDate >= :startDate 
            GROUP BY a.departmentId 
            ORDER BY count DESC
            """;
        
        Query query = entityManager.createQuery(sql);
        query.setParameter("startDate", LocalDate.now().minusDays(30));
        query.setMaxResults(10);
        
        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> ranking = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            String deptId = (String) row[0];
            item.put("departmentId", deptId);
            item.put("count", row[1]);
            
            departmentRepository.findById(deptId)
                    .ifPresent(dept -> item.put("departmentName", dept.getName()));
            
            ranking.add(item);
        }
        
        return ranking;
    }
    
    private List<Map<String, Object>> getDoctorWorkload() {
        String sql = """
            SELECT a.doctorId, COUNT(a) as count 
            FROM Appointment a 
            WHERE a.appointmentDate >= :startDate AND a.status IN ('CHECKED_IN', 'COMPLETED')
            GROUP BY a.doctorId 
            ORDER BY count DESC
            """;
        
        Query query = entityManager.createQuery(sql);
        query.setParameter("startDate", LocalDate.now().minusDays(30));
        query.setMaxResults(10);
        
        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> workload = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            String doctorId = (String) row[0];
            item.put("doctorId", doctorId);
            item.put("count", row[1]);
            
            doctorRepository.findById(doctorId)
                    .ifPresent(doctor -> {
                        item.put("doctorName", doctor.getName());
                        item.put("title", doctor.getTitle().name());
                    });
            
            workload.add(item);
        }
        
        return workload;
    }
    
    public Map<String, Object> getAppointmentTrend(int days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());
            counts.add(countAppointmentsByDate(date, null));
        }
        
        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }
}
