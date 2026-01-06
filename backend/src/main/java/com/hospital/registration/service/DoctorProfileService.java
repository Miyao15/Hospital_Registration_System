package com.hospital.registration.service;

import com.hospital.registration.dto.*;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Hospital;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.entity.MedicalItem;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.DoctorReviewRepository;
import com.hospital.registration.repository.ExaminationItemDepartmentRepository;
import com.hospital.registration.repository.HospitalRepository;
import com.hospital.registration.repository.MedicalItemRepository;
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
    private final ExaminationItemDepartmentRepository examinationItemDepartmentRepository;
    private final HospitalRepository hospitalRepository;
    private final MedicalItemRepository medicalItemRepository;
    
    public List<DoctorListDTO> getDoctorsByDepartment(String departmentId) {
        try {
            log.info("Fetching active doctors by department ID: {}", departmentId);
            // 只返回status为ACTIVE的医生
            List<Doctor> doctors = doctorRepository.findByDepartmentIdAndUserStatus(departmentId, UserStatus.ACTIVE.name());
            log.info("Found {} active doctors in department", doctors.size());
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
            log.info("Fetching all active doctors with page: {} and size: {}", page, size);
            // 只返回status为ACTIVE的医生
            Page<Doctor> doctors = doctorRepository.findAllByUserStatus(UserStatus.ACTIVE.name(), pageable);
            log.info("Found {} active doctors", doctors.getTotalElements());
            return doctors.map(this::safeConvertToListDTO);
        } catch (Exception e) {
            log.error("Error fetching all doctors with page: {} and size: {}, error: {}", page, size, e.getMessage(), e);
            // 返回空页而不是抛出异常
            return Page.empty(pageable);
        }
    }
    
    // 管理员专用：获取所有状态的医生（包括PENDING、ACTIVE、LOCKED等）
    public Page<DoctorListDTO> getAllDoctorsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            log.info("Admin fetching all doctors (all statuses) with page: {} and size: {}", page, size);
            // 返回所有状态的医生
            Page<Doctor> doctors = doctorRepository.findAll(pageable);
            log.info("Found {} doctors (all statuses)", doctors.getTotalElements());
            return doctors.map(this::safeConvertToListDTO);
        } catch (Exception e) {
            log.error("Error fetching all doctors for admin with page: {} and size: {}, error: {}", page, size, e.getMessage(), e);
            // 返回空页而不是抛出异常
            return Page.empty(pageable);
        }
    }
    
    // 安全的转换方法，捕获单个医生转换的错误
    private DoctorListDTO safeConvertToListDTO(Doctor doctor) {
        try {
            if (doctor == null) {
                log.error("Doctor is null in safeConvertToListDTO");
                return null;
            }
            DoctorListDTO dto = convertToListDTO(doctor);
            if (dto == null) {
                log.error("convertToListDTO returned null for doctor: {}", doctor.getId());
                // 返回一个基本的 DTO
                dto = new DoctorListDTO();
                dto.setId(doctor.getId());
                dto.setName(doctor.getName() != null ? doctor.getName() : "未知");
                dto.setTitle("医生");
                dto.setRating(5.0);
                dto.setReviewCount(0);
            }
            return dto;
        } catch (Exception e) {
            log.error("Error converting doctor {} to DTO: {}", 
                doctor != null ? doctor.getId() : "null", e.getMessage(), e);
            // 返回一个基本的 DTO，确保不会返回null
            DoctorListDTO dto = new DoctorListDTO();
            if (doctor != null) {
                dto.setId(doctor.getId());
                dto.setName(doctor.getName() != null ? doctor.getName() : "未知");
                dto.setTitle("医生");
                dto.setEmployeeId(doctor.getEmployeeId());
                dto.setLicenseNumber(doctor.getLicenseNumber());
            }
            dto.setOnlineStatus("AVAILABLE");
            dto.setStatus("ACTIVE");
            dto.setRating(5.0);
            dto.setReviewCount(0);
            return dto;
        }
    }
    
    public DoctorDetailDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医生不存在"));
        
        // 检查医生的用户状态，只有ACTIVE状态的医生才能被查看
        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new BusinessException("医生用户信息不存在"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("医生不存在");
        }
        
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
            log.info("Searching doctors with departmentId: {}, keyword: {}, medicalItemId: {}, region: {}, city: {}, district: {}", 
                    searchDTO.getDepartmentId(), searchDTO.getKeyword(), searchDTO.getMedicalItemId(), 
                    searchDTO.getRegion(), searchDTO.getCity(), searchDTO.getDistrict());
            
            // 先按地区和城市筛选医院，获取符合条件的医院ID列表
            final List<String> allowedHospitalIds;
            
            if ((searchDTO.getRegion() != null && !searchDTO.getRegion().isEmpty()) || 
                (searchDTO.getCity() != null && !searchDTO.getCity().isEmpty()) ||
                (searchDTO.getProvince() != null && !searchDTO.getProvince().isEmpty()) ||
                (searchDTO.getDistrict() != null && !searchDTO.getDistrict().isEmpty())) {
                
                List<Hospital> hospitals = new ArrayList<>();
                
                if (searchDTO.getDistrict() != null && !searchDTO.getDistrict().isEmpty()) {
                    // 优先使用区县筛选（最精确）
                    if (searchDTO.getCity() != null && !searchDTO.getCity().isEmpty()) {
                        // 同时指定城市和区县
                        hospitals = hospitalRepository.findByCityAndDistrictAndEnabledTrue(
                                searchDTO.getCity(), searchDTO.getDistrict());
                    } else {
                        // 只指定区县
                        hospitals = hospitalRepository.findByDistrictAndEnabledTrue(searchDTO.getDistrict());
                    }
                } else if (searchDTO.getRegion() != null && !searchDTO.getRegion().isEmpty() && 
                    searchDTO.getCity() != null && !searchDTO.getCity().isEmpty()) {
                    // 同时指定地区和城市
                    hospitals = hospitalRepository.findByRegionAndCityAndEnabledTrue(
                            searchDTO.getRegion(), searchDTO.getCity());
                } else if (searchDTO.getRegion() != null && !searchDTO.getRegion().isEmpty()) {
                    // 只指定地区
                    hospitals = hospitalRepository.findByRegionAndEnabledTrue(searchDTO.getRegion());
                } else if (searchDTO.getCity() != null && !searchDTO.getCity().isEmpty()) {
                    // 只指定城市
                    hospitals = hospitalRepository.findByCityAndEnabledTrue(searchDTO.getCity());
                } else if (searchDTO.getProvince() != null && !searchDTO.getProvince().isEmpty()) {
                    // 只指定省份
                    hospitals = hospitalRepository.findByProvinceAndEnabledTrue(searchDTO.getProvince());
                }
                
                allowedHospitalIds = hospitals.stream()
                        .map(Hospital::getId)
                        .collect(Collectors.toList());
                
                log.info("Found {} hospitals matching location criteria", allowedHospitalIds.size());
                
                // 如果没有找到符合条件的医院，直接返回空结果
                if (allowedHospitalIds.isEmpty()) {
                    return Page.empty(pageable);
                }
            } else {
                allowedHospitalIds = null;
            }
            
            Page<Doctor> doctors;
            
            // 如果提供了检查项目ID，先查询检查项目信息
            if (searchDTO.getMedicalItemId() != null && !searchDTO.getMedicalItemId().isEmpty()) {
                // 查询检查项目信息，判断是否是"健康体检"
                MedicalItem medicalItem = medicalItemRepository.findById(searchDTO.getMedicalItemId()).orElse(null);
                boolean isHealthCheckup = false;
                if (medicalItem != null) {
                    // 判断是否是"健康体检"（通过名称或ID判断）
                    isHealthCheckup = "健康体检".equals(medicalItem.getName()) || "item-001".equals(medicalItem.getId());
                }
                
                // 如果是"健康体检"，直接返回所有已审核的医生（所有科室都可以进行体检）
                if (isHealthCheckup) {
                    log.info("Health checkup item detected, returning all active doctors");
                    Page<Doctor> allActiveDoctors = doctorRepository.findAllByUserStatus(UserStatus.ACTIVE.name(), pageable);
                    // 应用医院筛选
                    if (allowedHospitalIds != null) {
                        final List<String> finalIds = allowedHospitalIds;
                        List<Doctor> filteredDoctors = allActiveDoctors.getContent().stream()
                                .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                                .collect(Collectors.toList());
                        doctors = new org.springframework.data.domain.PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
                    } else {
                        doctors = allActiveDoctors;
                    }
                } else {
                    // 其他检查项目，通过科室关联查询
                    List<String> departmentIds = examinationItemDepartmentRepository
                            .findDepartmentIdsByExaminationItemId(searchDTO.getMedicalItemId());
                    log.info("Found {} departments for medical item {}", departmentIds.size(), searchDTO.getMedicalItemId());
                    
                    if (departmentIds.isEmpty()) {
                        // 如果没有关联的科室，返回空结果
                        return Page.empty(pageable);
                    }
                
                // 如果同时指定了departmentId，则取交集
                if (searchDTO.getDepartmentId() != null && !searchDTO.getDepartmentId().isEmpty()) {
                    if (!departmentIds.contains(searchDTO.getDepartmentId())) {
                        // 指定的科室不在关联列表中，返回空结果
                        return Page.empty(pageable);
                    }
                    // 使用指定的科室ID，只返回ACTIVE状态的医生
                    List<Doctor> deptDoctors = doctorRepository.findByDepartmentIdAndUserStatus(searchDTO.getDepartmentId(), UserStatus.ACTIVE.name());
                    // 应用医院筛选
                    if (allowedHospitalIds != null) {
                        final List<String> finalIds = allowedHospitalIds;
                        deptDoctors = deptDoctors.stream()
                                .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                                .collect(Collectors.toList());
                    }
                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + pageable.getPageSize(), deptDoctors.size());
                    List<Doctor> pageContent = start < deptDoctors.size() ? deptDoctors.subList(start, end) : new ArrayList<>();
                    doctors = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, deptDoctors.size());
                } else {
                    // 查询所有关联科室的医生，只返回ACTIVE状态的医生
                    List<Doctor> allDeptDoctors = new ArrayList<>();
                    for (String deptId : departmentIds) {
                        allDeptDoctors.addAll(doctorRepository.findByDepartmentIdAndUserStatus(deptId, UserStatus.ACTIVE.name()));
                    }
                    // 应用医院筛选
                    if (allowedHospitalIds != null) {
                        final List<String> finalIds = allowedHospitalIds;
                        allDeptDoctors = allDeptDoctors.stream()
                                .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                                .collect(Collectors.toList());
                    }
                    log.info("Found {} doctors across {} departments", allDeptDoctors.size(), departmentIds.size());
                    // 手动分页
                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + pageable.getPageSize(), allDeptDoctors.size());
                    List<Doctor> pageContent = start < allDeptDoctors.size() ? allDeptDoctors.subList(start, end) : new ArrayList<>();
                    doctors = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allDeptDoctors.size());
                }
                }
            } else if (searchDTO.getDepartmentId() != null && !searchDTO.getDepartmentId().isEmpty()) {
                // 按科室查询，只返回ACTIVE状态的医生
                List<Doctor> deptDoctors = doctorRepository.findByDepartmentIdAndUserStatus(searchDTO.getDepartmentId(), UserStatus.ACTIVE.name());
                // 应用医院筛选
                if (allowedHospitalIds != null) {
                    final List<String> finalIds = allowedHospitalIds;
                    deptDoctors = deptDoctors.stream()
                            .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                            .collect(Collectors.toList());
                }
                log.info("Found {} doctors in department {}", deptDoctors.size(), searchDTO.getDepartmentId());
                // 手动分页
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), deptDoctors.size());
                List<Doctor> pageContent = start < deptDoctors.size() ? deptDoctors.subList(start, end) : new ArrayList<>();
                doctors = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, deptDoctors.size());
            } else if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
                // 按关键字查询，只返回ACTIVE状态的医生
                Page<Doctor> keywordDoctors = doctorRepository.findByNameContainingAndUserStatus(searchDTO.getKeyword(), UserStatus.ACTIVE.name(), pageable);
                // 应用医院筛选
                if (allowedHospitalIds != null) {
                    final List<String> finalIds = allowedHospitalIds;
                    List<Doctor> filteredDoctors = keywordDoctors.getContent().stream()
                            .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                            .collect(Collectors.toList());
                    doctors = new org.springframework.data.domain.PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
                } else {
                    doctors = keywordDoctors;
                }
            } else {
                // 返回所有医生，只返回ACTIVE状态的医生
                Page<Doctor> allDoctorsPage = doctorRepository.findAllByUserStatus(UserStatus.ACTIVE.name(), pageable);
                // 应用医院筛选
                if (allowedHospitalIds != null) {
                    final List<String> finalIds = allowedHospitalIds;
                    List<Doctor> filteredDoctors = allDoctorsPage.getContent().stream()
                            .filter(doc -> doc.getHospitalId() != null && finalIds.contains(doc.getHospitalId()))
                            .collect(Collectors.toList());
                    doctors = new org.springframework.data.domain.PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
                } else {
                    doctors = allDoctorsPage;
                }
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
            log.info("Fetching top active doctors with limit: {}", limit);
            // 获取所有ACTIVE状态的医生并转换为DTO
            List<Doctor> allDoctors = doctorRepository.findAllByUserStatusList(UserStatus.ACTIVE.name());
            log.info("Found {} active doctors in database", allDoctors.size());
            
            if (allDoctors.isEmpty()) {
                log.warn("No doctors found in database!");
                return new ArrayList<>();
            }
            
            // 记录前几个医生的ID用于调试
            if (allDoctors.size() > 0) {
                log.info("First few doctor IDs: {}", allDoctors.stream()
                    .limit(3)
                    .map(Doctor::getId)
                    .collect(Collectors.toList()));
            }
            
            List<DoctorListDTO> allDoctorsDTO = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;
            
            for (Doctor doctor : allDoctors) {
                try {
                    DoctorListDTO dto = safeConvertToListDTO(doctor);
                    if (dto != null) {
                        allDoctorsDTO.add(dto);
                        successCount++;
                    } else {
                        failCount++;
                        log.warn("safeConvertToListDTO returned null for doctor: {}", doctor.getId());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("Error converting doctor {} to DTO: {}", doctor.getId(), e.getMessage(), e);
                }
            }
            
            log.info("Converted {} doctors to DTO (success: {}, failed: {})", 
                allDoctorsDTO.size(), successCount, failCount);
            
            if (allDoctorsDTO.isEmpty()) {
                log.error("All doctors failed to convert to DTO!");
                return new ArrayList<>();
            }
            
            // 按评分降序排序，返回评分最高的医生
            List<DoctorListDTO> topDoctors = allDoctorsDTO.stream()
                    .sorted((a, b) -> {
                        // 按评分降序，然后按评价数量降序
                        double ratingA = a.getRating() != null ? a.getRating() : 5.0;
                        double ratingB = b.getRating() != null ? b.getRating() : 5.0;
                        int ratingCompare = Double.compare(ratingB, ratingA);
                        if (ratingCompare != 0) return ratingCompare;
                        return Integer.compare(
                            b.getReviewCount() != null ? b.getReviewCount() : 0,
                            a.getReviewCount() != null ? a.getReviewCount() : 0
                        );
                    })
                    .limit(limit != null ? limit : 10)
                    .collect(Collectors.toList());
            
            log.info("Returning {} top doctors", topDoctors.size());
            if (!topDoctors.isEmpty()) {
                log.info("Top doctor: id={}, name={}, rating={}, reviewCount={}", 
                    topDoctors.get(0).getId(),
                    topDoctors.get(0).getName(),
                    topDoctors.get(0).getRating(),
                    topDoctors.get(0).getReviewCount());
            }
            return topDoctors;
        } catch (Exception e) {
            log.error("Error fetching top doctors: {}", e.getMessage(), e);
            e.printStackTrace(); // 打印完整堆栈
            return new ArrayList<>(); // 返回空列表
        }
    }

    public List<DoctorListDTO> getDoctorsBySpecialty(String specialty) {
        try {
            // 只返回ACTIVE状态的医生
            return doctorRepository.findAllByUserStatusList(UserStatus.ACTIVE.name()).stream()
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
        dto.setEmployeeId(doctor.getEmployeeId());        // 工号
        dto.setLicenseNumber(doctor.getLicenseNumber());  // 执业证号
        dto.setDepartmentId(doctor.getDepartmentId());
        // 优先使用 avatarData，如果没有则使用 avatarUrl
        String avatar = doctor.getAvatarData() != null ? doctor.getAvatarData() : doctor.getAvatarUrl();
        dto.setAvatarUrl(avatar);
        dto.setAvatarData(doctor.getAvatarData());
        dto.setSpecialty(doctor.getSpecialty() != null ? doctor.getSpecialty() : "");
        dto.setScheduleInfo(doctor.getScheduleInfo());
        dto.setOnlineStatus("AVAILABLE");
        dto.setDepartmentName(""); // 默认值
        
        // 获取用户状态
        try {
            userRepository.findById(doctor.getUserId()).ifPresent(user -> {
                dto.setStatus(user.getStatus().name());  // 设置医生状态
                dto.setPhone(user.getPhone());           // 设置手机号
            });
        } catch (Exception e) {
            log.warn("Could not get user info for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            dto.setStatus("ACTIVE"); // 默认状态
        }
        
        // 获取科室名称
        if (doctor.getDepartmentId() != null && !doctor.getDepartmentId().isEmpty()) {
            try {
                departmentRepository.findById(doctor.getDepartmentId())
                        .ifPresent(dept -> dto.setDepartmentName(dept.getName()));
            } catch (Exception e) {
                log.warn("Could not get department name for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            }
        }
        
        // 获取评价统计
        try {
            Double avgRating = doctorReviewRepository.getAverageRatingByDoctorId(doctor.getId());
            Integer reviewCount = doctorReviewRepository.getReviewCountByDoctorId(doctor.getId());
            log.info("Doctor {} - avgRating: {}, reviewCount: {}", doctor.getId(), avgRating, reviewCount);
            // 只有当没有评价记录时才使用默认值，如果有评价记录但avgRating为null，则说明有问题
            if (reviewCount != null && reviewCount > 0) {
                // 有评价记录，必须使用实际的平均评分
                if (avgRating == null) {
                    log.error("Doctor {} has {} reviews but avgRating is null!", doctor.getId(), reviewCount);
                    // 如果没有平均分但有评价记录，重新计算或使用0
                    dto.setRating(0.0);
                } else {
                    dto.setRating(avgRating);
                }
                dto.setReviewCount(reviewCount);
            } else {
                // 没有评价记录，使用默认值
                dto.setRating(5.0);
                dto.setReviewCount(0);
            }
        } catch (Exception e) {
            log.warn("Could not get review stats for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            dto.setRating(5.0);
            dto.setReviewCount(0);
        }
        
        // 获取医院信息
        if (doctor.getHospitalId() != null && !doctor.getHospitalId().isEmpty()) {
            try {
                hospitalRepository.findById(doctor.getHospitalId()).ifPresent(hospital -> {
                    dto.setHospitalId(hospital.getId());
                    dto.setHospitalName(hospital.getName());
                    dto.setHospitalAddress(hospital.getAddress());
                    dto.setHospitalLongitude(hospital.getLongitude());
                    dto.setHospitalLatitude(hospital.getLatitude());
                    dto.setHospitalCity(hospital.getCity());
                    dto.setHospitalRegion(hospital.getRegion());
                    dto.setHospitalProvince(hospital.getProvince());
                });
            } catch (Exception e) {
                log.warn("Could not get hospital info for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            }
        }
        
        return dto;
    }
    
    private DoctorDetailDTO convertToDetailDTO(Doctor doctor) {
        DoctorDetailDTO dto = new DoctorDetailDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setTitle(getTitleDisplayName(doctor.getTitle()));
        dto.setEmployeeId(doctor.getEmployeeId());        // 添加工号
        dto.setLicenseNumber(doctor.getLicenseNumber());  // 添加执业证号
        dto.setDepartmentId(doctor.getDepartmentId());
        // 优先使用 avatarData，如果没有则使用 avatarUrl
        String avatar = doctor.getAvatarData() != null ? doctor.getAvatarData() : doctor.getAvatarUrl();
        dto.setAvatarUrl(avatar);
        dto.setAvatarData(doctor.getAvatarData());
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
            log.info("Doctor {} (detail) - avgRating: {}, reviewCount: {}", doctor.getId(), avgRating, reviewCount);
            // 只有当没有评价记录时才使用默认值，如果有评价记录但avgRating为null，则说明有问题
            if (reviewCount != null && reviewCount > 0) {
                // 有评价记录，必须使用实际的平均评分
                if (avgRating == null) {
                    log.error("Doctor {} has {} reviews but avgRating is null!", doctor.getId(), reviewCount);
                    // 如果没有平均分但有评价记录，重新计算或使用0
                    dto.setAvgRating(0.0);
                    dto.setRating(0.0);
                } else {
                    dto.setAvgRating(avgRating);
                    dto.setRating(avgRating);
                }
                dto.setReviewCount(reviewCount);
            } else {
                // 没有评价记录，使用默认值
                dto.setAvgRating(5.0);
                dto.setReviewCount(0);
                dto.setRating(5.0);
            }
        } catch (Exception e) {
            log.warn("Could not get review stats for doctor: {}, error: {}", doctor.getId(), e.getMessage());
            dto.setAvgRating(5.0);
            dto.setReviewCount(0);
            dto.setRating(5.0);
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
    
    /**
     * 根据用户ID获取医生详情
     */
    public DoctorDetailDTO getDoctorProfileByUserId(String userId) {
        log.info("根据userId获取医生资料: {}", userId);
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        return convertToDetailDTO(doctor);
    }
    
    /**
     * 更新医生个人资料
     */
    public DoctorDetailDTO updateDoctorProfile(String userId, UpdateDoctorProfileDTO dto) {
        log.info("更新医生资料: userId={}", userId);
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        // 更新字段
        if (dto.getName() != null) {
            doctor.setName(dto.getName());
        }
        if (dto.getTitle() != null) {
            try {
                doctor.setTitle(DoctorTitle.valueOf(dto.getTitle()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的职称: {}", dto.getTitle());
            }
        }
        if (dto.getLicenseNumber() != null) {
            doctor.setLicenseNumber(dto.getLicenseNumber());
        }
        if (dto.getSpecialty() != null) {
            doctor.setSpecialty(dto.getSpecialty());
        }
        if (dto.getIntroduction() != null) {
            doctor.setIntroduction(dto.getIntroduction());
        }
        if (dto.getEducation() != null) {
            doctor.setEducation(dto.getEducation());
        }
        if (dto.getAvatarUrl() != null) {
            doctor.setAvatarUrl(dto.getAvatarUrl());
        }
        
        doctor = doctorRepository.save(doctor);
        return convertToDetailDTO(doctor);
    }
}
