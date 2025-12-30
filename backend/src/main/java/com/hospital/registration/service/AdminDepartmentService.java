package com.hospital.registration.service;

import com.hospital.registration.dto.CreateDepartmentDTO;
import com.hospital.registration.dto.DepartmentDTO;
import com.hospital.registration.entity.Department;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    public List<DepartmentDTO> getAllDepartments() {
        try {
            return departmentRepository.findAll().stream()
                    .sorted((a, b) -> {
                        if (a == null) return 1;
                        if (b == null) return -1;
                        if (a.getSortOrder() == null) return 1;
                        if (b.getSortOrder() == null) return -1;
                        return a.getSortOrder().compareTo(b.getSortOrder());
                    })
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("获取科室列表失败: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }
    
    @Transactional
    public DepartmentDTO createDepartment(CreateDepartmentDTO dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new BusinessException("科室名称已存在");
        }
        
        Department department = new Department();
        department.setId(UUID.randomUUID().toString());
        department.setName(dto.getName());
        department.setCategory(dto.getCategory());
        department.setDescription(dto.getDescription());
        department.setLocation(dto.getLocation());
        department.setPhone(dto.getPhone());
        department.setDirectorId(dto.getDirectorId());
        department.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        department.setEnabled(true);
        
        department = departmentRepository.save(department);
        
        return convertToDTO(department);
    }
    
    @Transactional
    public DepartmentDTO updateDepartment(String id, CreateDepartmentDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("科室不存在"));
        
        // 检查名称是否重复（排除自身）
        if (!department.getName().equals(dto.getName()) && 
            departmentRepository.existsByName(dto.getName())) {
            throw new BusinessException("科室名称已存在");
        }
        
        department.setName(dto.getName());
        department.setCategory(dto.getCategory());
        department.setDescription(dto.getDescription());
        department.setLocation(dto.getLocation());
        department.setPhone(dto.getPhone());
        department.setDirectorId(dto.getDirectorId());
        if (dto.getSortOrder() != null) {
            department.setSortOrder(dto.getSortOrder());
        }
        
        department = departmentRepository.save(department);
        
        return convertToDTO(department);
    }
    
    @Transactional
    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("科室不存在"));
        
        // 软删除：禁用科室
        department.setEnabled(false);
        departmentRepository.save(department);
    }
    
    @Transactional
    public void enableDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("科室不存在"));
        
        department.setEnabled(true);
        departmentRepository.save(department);
    }
    
    private DepartmentDTO convertToDTO(Department department) {
        if (department == null) {
            return null;
        }
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCategory(department.getCategory());
        dto.setDescription(department.getDescription());
        dto.setLocation(department.getLocation());
        dto.setPhone(department.getPhone());
        dto.setEnabled(department.getEnabled() != null ? department.getEnabled() : true);
        dto.setSortOrder(department.getSortOrder());
        return dto;
    }
}
