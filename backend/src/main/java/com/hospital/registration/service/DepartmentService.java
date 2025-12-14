package com.hospital.registration.service;

import com.hospital.registration.dto.DepartmentDTO;
import com.hospital.registration.dto.DepartmentListDTO;
import com.hospital.registration.dto.DoctorSimpleDTO;
import com.hospital.registration.entity.Department;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    
    public List<DepartmentListDTO> getAllDepartmentsGroupedByCategory() {
        List<Department> departments = departmentRepository.findByEnabledTrueOrderBySortOrderAsc();
        
        Map<String, List<Department>> grouped = departments.stream()
                .collect(Collectors.groupingBy(Department::getCategory));
        
        List<DepartmentListDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Department>> entry : grouped.entrySet()) {
            List<DepartmentDTO> deptDTOs = entry.getValue().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            result.add(new DepartmentListDTO(entry.getKey(), deptDTOs));
        }
        return result;
    }
    
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findByEnabledTrueOrderBySortOrderAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DepartmentDTO getDepartmentById(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("科室不存在"));
        return convertToDTO(department);
    }
    
    public List<DepartmentDTO> getDepartmentsByCategory(String category) {
        return departmentRepository.findByCategoryAndEnabledTrueOrderBySortOrderAsc(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCategory(department.getCategory());
        dto.setDescription(department.getDescription());
        dto.setLocation(department.getLocation());
        dto.setPhone(department.getPhone());
        
        // 获取科室医生数量
        int doctorCount = doctorRepository.countByDepartmentId(department.getId());
        dto.setDoctorCount(doctorCount);
        
        // 获取科室主任信息
        if (department.getDirectorId() != null) {
            doctorRepository.findById(department.getDirectorId())
                    .ifPresent(director -> {
                        DoctorSimpleDTO directorDTO = new DoctorSimpleDTO();
                        directorDTO.setId(director.getId());
                        directorDTO.setName(director.getName());
                        directorDTO.setTitle(director.getTitle().name());
                        directorDTO.setAvatarUrl(director.getAvatarUrl());
                        dto.setDirector(directorDTO);
                    });
        }
        
        return dto;
    }
}
