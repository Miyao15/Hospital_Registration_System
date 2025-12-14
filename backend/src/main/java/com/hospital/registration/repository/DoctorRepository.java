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
    
    @Query("SELECT d FROM Doctor d JOIN User u ON d.userId = u.id WHERE u.status = 'ACTIVE' AND d.departmentId = :departmentId")
    List<Doctor> findActiveDoctorsByDepartmentId(@Param("departmentId") String departmentId);
    
    @Query("SELECT d FROM Doctor d JOIN User u ON d.userId = u.id WHERE u.status = 'ACTIVE'")
    Page<Doctor> findAllActiveDoctors(Pageable pageable);
    
    @Query("SELECT d FROM Doctor d JOIN User u ON d.userId = u.id WHERE u.status = 'ACTIVE' AND d.name LIKE %:keyword%")
    Page<Doctor> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
}
