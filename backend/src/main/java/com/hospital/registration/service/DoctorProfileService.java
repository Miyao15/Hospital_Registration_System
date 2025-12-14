package com.hospital.registration.service;

import com.hospital.registration.dto.*;
import com.hospital.registration.entity.Department;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorProfileService {
    
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorReviewRepository doctorReviewRepository;
    private final UserRepository userRepository;
    
    public List<DoctorListDTO> getDoctorsByDepartment(String departmentId) {
        List<Doctor> doctors = doctorRepository.findActiveDoctorsByDepartmentId(departmentId);
        return doctors.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }
    
    public Page<DoctorListDTO> getAllDoctors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> doctors = doctorRepository.findAllActiveDoctors(pageable);
        return doctors.map(this::convertToListDTO);
    }
    
    public DoctorDetailDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医生不存在"));
        return convertToDetailDTO(doctor);
    }
    
    public Page<DoctorListDTO> searchDoctors(DoctorSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(
                searchDTO.getPage() != null ? searchDTO.getPage() : 0,
                searchDTO.getSize() != null ? searchDTO.getSize() : 10
        );
        
        Specification<Doctor> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索（姓名）
            if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + searchDTO.getKeyword() + "%"));
            }
            
            // 科室筛选
            if (searchDTO.getDepartmentId() != null && !searchDTO.getDepartmentId().isEmpty()) {
                predicates.add(cb.equal(root.get("departmentId"), searchDTO.getDepartmentId()));
            }
            
            // 职称筛选
            if (searchDTO.getTitle() != null && !searchDTO.getTitle().isEmpty()) {
                try {
                    DoctorTitle title = DoctorTitle.valueOf(searchDTO.getTitle());
                    predicates.add(cb.equal(root.get("title"), title));
                } catch (IllegalArgumentException ignored) {
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Doctor> doctors = doctorRepository.findAll(spec, pageable);
        return doctors.map(this::convertToListDTO);
    }
    
    public String getDoctorOnlineStatus(String doctorId) {
        // 简化实现：根据用户状态判断
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return "REST";
        }
        
        User user = userRepository.findById(doctor.getUserId()).orElse(null);
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            return "REST";
        }
        
        // TODO: 后续可以根据排班和号源情况判断 AVAILABLE/FULL
        return "AVAILABLE";
    }
    
    private DoctorListDTO convertToListDTO(Doctor doctor) {
        DoctorListDTO dto = new DoctorListDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setTitle(getTitleDisplayName(doctor.getTitle()));
        dto.setDepartmentId(doctor.getDepartmentId());
        dto.setAvatarUrl(doctor.getAvatarUrl());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setScheduleInfo(doctor.getScheduleInfo());
        dto.setOnlineStatus(getDoctorOnlineStatus(doctor.getId()));
        
        // 获取科室名称
        departmentRepository.findById(doctor.getDepartmentId())
                .ifPresent(dept -> dto.setDepartmentName(dept.getName()));
        
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
        dto.setOnlineStatus(getDoctorOnlineStatus(doctor.getId()));
        
        // 获取科室名称
        departmentRepository.findById(doctor.getDepartmentId())
                .ifPresent(dept -> dto.setDepartmentName(dept.getName()));
        
        // 获取评价统计
        dto.setAvgRating(doctorReviewRepository.getAverageRatingByDoctorId(doctor.getId()));
        dto.setReviewCount(doctorReviewRepository.getReviewCountByDoctorId(doctor.getId()));
        
        return dto;
    }
    
    private String getTitleDisplayName(DoctorTitle title) {
        return switch (title) {
            case RESIDENT -> "住院医师";
            case ATTENDING -> "主治医师";
            case ASSOCIATE_CHIEF -> "副主任医师";
            case CHIEF -> "主任医师";
        };
    }
}
