package com.hospital.registration.service;

import com.hospital.registration.dto.*;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.DoctorReviewRepository;
import com.hospital.registration.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorProfileService {
    
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorReviewRepository doctorReviewRepository;
    private final UserRepository userRepository;
    
    public List<DoctorListDTO> getDoctorsByDepartment(String departmentId) {
        try {
            log.info("Fetching doctors by department ID: {}", departmentId);
            List<Doctor> doctors = doctorRepository.findByDepartmentId(departmentId);
            log.info("Found {} doctors in department", doctors.size());
            return doctors.stream()
                    .map(this::safeConvertToListDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching doctors by department ID: {}, error: {}", departmentId, e.getMessage(), e);
            return new ArrayList<>(); // 返回空列表而不是抛出异常
        }
    }
    
    public Page<DoctorListDTO> getAllDoctors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            log.info("Fetching all doctors with page: {} and size: {}", page, size);
            Page<Doctor> doctors = doctorRepository.findAll(pageable);
            log.info("Found {} doctors", doctors.getTotalElements());
            return doctors.map(this::safeConvertToListDTO);
        } catch (Exception e) {
            log.error("Error fetching all doctors with page: {} and size: {}, error: {}", page, size, e.getMessage(), e);
            // 返回空页而不是抛出异常
            return Page.empty(pageable);
        }
    }
    
    // 安全的转换方法，捕获单个医生转换的错误
    private DoctorListDTO safeConvertToListDTO(Doctor doctor) {
        try {
            return convertToListDTO(doctor);
        } catch (Exception e) {
            log.error("Error converting doctor {} to DTO: {}", doctor.getId(), e.getMessage());
            // 返回一个基本的 DTO
            DoctorListDTO dto = new DoctorListDTO();
            dto.setId(doctor.getId());
            dto.setName(doctor.getName() != null ? doctor.getName() : "未知");
            dto.setTitle("医生");
            dto.setOnlineStatus("AVAILABLE");
            return dto;
        }
    }
    
    public DoctorDetailDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医生不存在"));
        try {
            return convertToDetailDTO(doctor);
        } catch (Exception e) {
            log.error("Error converting Doctor to DoctorDetailDTO for doctor ID: {}", id, e);
            throw new BusinessException("无法获取医生详情，请稍后重试。");
        }
    }
    
    public Page<DoctorListDTO> searchDoctors(DoctorSearchDTO searchDTO) {
        int page = searchDTO.getPage() != null ? searchDTO.getPage() : 0;
        int size = searchDTO.getSize() != null ? searchDTO.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);
        
        try {
            log.info("Searching doctors with departmentId: {}, keyword: {}", searchDTO.getDepartmentId(), searchDTO.getKeyword());
            
            Page<Doctor> doctors;
            
            // 简化查询逻辑
            if (searchDTO.getDepartmentId() != null && !searchDTO.getDepartmentId().isEmpty()) {
                // 按科室查询
                List<Doctor> deptDoctors = doctorRepository.findByDepartmentId(searchDTO.getDepartmentId());
                log.info("Found {} doctors in department {}", deptDoctors.size(), searchDTO.getDepartmentId());
                // 手动分页
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), deptDoctors.size());
                List<Doctor> pageContent = start < deptDoctors.size() ? deptDoctors.subList(start, end) : new ArrayList<>();
                doctors = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, deptDoctors.size());
            } else if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
                // 按关键字查询
                doctors = doctorRepository.findByNameContaining(searchDTO.getKeyword(), pageable);
            } else {
                // 返回所有医生
                doctors = doctorRepository.findAll(pageable);
            }
            
            return doctors.map(this::safeConvertToListDTO);
        } catch (Exception e) {
            log.error("Error searching doctors: {}", e.getMessage(), e);
            // 返回空页而不是抛出异常
            return Page.empty(pageable);
        }
    }
    
    public String getDoctorOnlineStatus(String doctorId) {
        // 简化实现，直接返回 AVAILABLE
        return "AVAILABLE";
    }
    
    public List<DoctorListDTO> getTopDoctors(Integer limit) {
        try {
            log.info("Fetching top doctors with limit: {}", limit);
            Pageable pageable = PageRequest.of(0, limit != null ? limit : 10);
            List<Doctor> doctors = doctorRepository.findAll(pageable).getContent();
            log.info("Found {} top doctors", doctors.size());
            return doctors.stream()
                    .map(this::safeConvertToListDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching top doctors: {}", e.getMessage(), e);
            return new ArrayList<>(); // 返回空列表
        }
    }

    public List<DoctorListDTO> getDoctorsBySpecialty(String specialty) {
        try {
            return doctorRepository.findAll().stream()
                    .filter(doctor -> doctor.getSpecialty() != null && 
                            doctor.getSpecialty().toLowerCase().contains(specialty.toLowerCase()))
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching doctors by specialty: {}", specialty, e);
            return new ArrayList<>();
        }
    }
    
    // 调试方法 - 获取医生总数
    public long getTotalDoctorCount() {
        log.info("DoctorProfileService.getTotalDoctorCount() 被调用");
        long count = doctorRepository.count();
        log.info("doctorRepository.count() 返回: {}", count);
        return count;
    }
    
    // 调试方法 - 原始查询所有医生
    public List<Doctor> debugFindAllRaw() {
        log.info("DoctorProfileService.debugFindAllRaw() 被调用");
        List<Doctor> doctors = doctorRepository.findAll();
        log.info("找到 {} 位医生", doctors.size());
        for (Doctor d : doctors) {
            log.info("医生: id={}, name={}, title={}, departmentId={}", 
                d.getId(), d.getName(), d.getTitle(), d.getDepartmentId());
        }
        return doctors;
    }
    
    // 调试方法 - 原生SQL查询
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;
    
    public Object debugNativeQuery() {
        log.info("执行原生SQL查询...");
        try {
            // 查询数据库名
            Object dbName = entityManager.createNativeQuery("SELECT DATABASE()").getSingleResult();
            log.info("当前数据库: {}", dbName);
            
            // 查询医生数量
            Object countResult = entityManager.createNativeQuery("SELECT COUNT(*) FROM doctors").getSingleResult();
            log.info("doctors表记录数: {}", countResult);
            
            // 查询所有医生
            @SuppressWarnings("unchecked")
            List<Object[]> doctors = entityManager.createNativeQuery("SELECT id, name, title FROM doctors").getResultList();
            log.info("查询到 {} 条医生记录", doctors.size());
            
            StringBuilder sb = new StringBuilder();
            sb.append("数据库: ").append(dbName).append("\n");
            sb.append("doctors表记录数: ").append(countResult).append("\n");
            sb.append("医生列表:\n");
            for (Object[] row : doctors) {
                sb.append("  - id=").append(row[0]).append(", name=").append(row[1]).append(", title=").append(row[2]).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("原生SQL查询失败: ", e);
            return "查询失败: " + e.getMessage();
        }
    }

    private DoctorListDTO convertToListDTO(Doctor doctor) {
        DoctorListDTO dto = new DoctorListDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName() != null ? doctor.getName() : "未知医生");
        dto.setTitle(getTitleDisplayName(doctor.getTitle()));
        dto.setDepartmentId(doctor.getDepartmentId());
        dto.setAvatarUrl(doctor.getAvatarUrl());
        dto.setSpecialty(doctor.getSpecialty() != null ? doctor.getSpecialty() : "");
        dto.setScheduleInfo(doctor.getScheduleInfo());
        dto.setOnlineStatus("AVAILABLE");
        dto.setDepartmentName(""); // 默认值
        
        // 获取科室名称
        if (doctor.getDepartmentId() != null && !doctor.getDepartmentId().isEmpty()) {
            try {
                departmentRepository.findById(doctor.getDepartmentId())
                        .ifPresent(dept -> dto.setDepartmentName(dept.getName()));
            } catch (Exception e) {
                log.warn("Could not get department name for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            }
        }
        
        return dto;
    }
    
    private DoctorDetailDTO convertToDetailDTO(Doctor doctor) {
        DoctorDetailDTO dto = new DoctorDetailDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setTitle(getTitleDisplayName(doctor.getTitle()));
        dto.setDepartmentId(doctor.getDepartmentId());
        dto.setAvatarUrl(doctor.getAvatarUrl());
        dto.setIntroduction(doctor.getIntroduction());
        dto.setEducation(doctor.getEducation());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setScheduleInfo(doctor.getScheduleInfo());
        dto.setOnlineStatus("AVAILABLE");
        
        // 获取科室名称
        try {
            if (doctor.getDepartmentId() != null) {
                departmentRepository.findById(doctor.getDepartmentId())
                        .ifPresent(dept -> dto.setDepartmentName(dept.getName()));
            }
        } catch (Exception e) {
            log.warn("Could not get department name for doctor: {}", doctor.getId());
        }
        
        // 获取评价统计
        try {
            Double avgRating = doctorReviewRepository.getAverageRatingByDoctorId(doctor.getId());
            Integer reviewCount = doctorReviewRepository.getReviewCountByDoctorId(doctor.getId());
            dto.setAvgRating(avgRating != null ? avgRating : 5.0);
            dto.setReviewCount(reviewCount != null ? reviewCount : 0);
        } catch (Exception e) {
            dto.setAvgRating(5.0);
            dto.setReviewCount(0);
        }
        
        return dto;
    }
    
    private String getTitleDisplayName(DoctorTitle title) {
        if (title == null) return "医生";
        return switch (title) {
            case RESIDENT -> "住院医师";
            case ATTENDING -> "主治医师";
            case ASSOCIATE_CHIEF -> "副主任医师";
            case DEPUTY_CHIEF_PHYSICIAN -> "副主任医师";
            case CHIEF -> "主任医师";
            default -> "医生";
        };
    }
}
