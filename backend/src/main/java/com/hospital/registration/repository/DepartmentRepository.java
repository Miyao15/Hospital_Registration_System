package com.hospital.registration.repository;

import com.hospital.registration.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    
    List<Department> findByEnabledTrueOrderBySortOrderAsc();
    
    List<Department> findByCategoryAndEnabledTrueOrderBySortOrderAsc(String category);
    
    @Query("SELECT DISTINCT d.category FROM Department d WHERE d.enabled = true ORDER BY d.category")
    List<String> findAllCategories();
    
    boolean existsByName(String name);
}
