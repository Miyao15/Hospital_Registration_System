package com.hospital.registration.repository;

import com.hospital.registration.entity.Doctor;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.UserStatus;
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
    
    // 查询状态为ACTIVE的医生（通过关联User表，使用原生SQL）
    @Query(value = "SELECT d.* FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE u.status = :status", 
           countQuery = "SELECT COUNT(*) FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE u.status = :status",
           nativeQuery = true)
    Page<Doctor> findAllByUserStatus(@Param("status") String status, Pageable pageable);
    
    @Query(value = "SELECT d.* FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE d.department_id = :departmentId AND u.status = :status",
           nativeQuery = true)
    List<Doctor> findByDepartmentIdAndUserStatus(@Param("departmentId") String departmentId, @Param("status") String status);
    
    @Query(value = "SELECT COUNT(*) FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE d.department_id = :departmentId AND u.status = :status",
           nativeQuery = true)
    int countByDepartmentIdAndUserStatus(@Param("departmentId") String departmentId, @Param("status") String status);
    
    @Query(value = "SELECT d.* FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE d.name LIKE %:name% AND u.status = :status",
           countQuery = "SELECT COUNT(*) FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE d.name LIKE %:name% AND u.status = :status",
           nativeQuery = true)
    Page<Doctor> findByNameContainingAndUserStatus(@Param("name") String name, @Param("status") String status, Pageable pageable);
    
    @Query(value = "SELECT d.* FROM doctors d INNER JOIN users u ON d.user_id = u.id WHERE u.status = :status",
           nativeQuery = true)
    List<Doctor> findAllByUserStatusList(@Param("status") String status);
    
    // 简化查询 - 先返回所有医生，后续再添加状态过滤
    default List<Doctor> findActiveDoctorsByDepartmentId(String departmentId) {
        return findByDepartmentIdAndUserStatus(departmentId, UserStatus.ACTIVE.name());
    }
    
    // 简化查询 - 返回所有医生
    default Page<Doctor> findAllActiveDoctors(Pageable pageable) {
        return findAllByUserStatus(UserStatus.ACTIVE.name(), pageable);
    }
    
    Page<Doctor> findByNameContaining(String name, Pageable pageable);
}
