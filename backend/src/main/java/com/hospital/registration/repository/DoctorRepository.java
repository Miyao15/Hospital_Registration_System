package com.hospital.registration.repository;

import com.hospital.registration.entity.Doctor;
import com.hospital.registration.enums.DoctorTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String>, JpaSpecificationExecutor<Doctor> {
    Optional<Doctor> findByUserId(String userId);
    Optional<Doctor> findByEmployeeId(String employeeId);
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByLicenseNumber(String licenseNumber);
    
    int countByDepartmentId(String departmentId);
    
    List<Doctor> findByDepartmentId(String departmentId);
    
    // 简化查询 - 先返回所有医生，后续再添加状态过滤
    default List<Doctor> findActiveDoctorsByDepartmentId(String departmentId) {
        return findByDepartmentId(departmentId);
    }
    
    // 简化查询 - 返回所有医生
    default Page<Doctor> findAllActiveDoctors(Pageable pageable) {
        return findAll(pageable);
    }
    
    Page<Doctor> findByNameContaining(String name, Pageable pageable);
}
