package com.hospital.registration.repository;

import com.hospital.registration.entity.ExaminationItemDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationItemDepartmentRepository extends JpaRepository<ExaminationItemDepartment, String> {
    
    /**
     * 根据检查项目ID查询关联的科室ID列表
     */
    @Query("SELECT eid.departmentId FROM ExaminationItemDepartment eid WHERE eid.examinationItemId = :examinationItemId")
    List<String> findDepartmentIdsByExaminationItemId(@Param("examinationItemId") String examinationItemId);
    
    /**
     * 根据科室ID查询关联的检查项目ID列表
     */
    @Query("SELECT eid.examinationItemId FROM ExaminationItemDepartment eid WHERE eid.departmentId = :departmentId")
    List<String> findExaminationItemIdsByDepartmentId(@Param("departmentId") String departmentId);
    
    /**
     * 删除检查项目的所有关联关系
     */
    void deleteByExaminationItemId(String examinationItemId);
    
    /**
     * 删除科室的所有关联关系
     */
    void deleteByDepartmentId(String departmentId);
    
    /**
     * 根据检查项目ID查询所有关联记录
     */
    List<ExaminationItemDepartment> findByExaminationItemId(String examinationItemId);
    
    /**
     * 根据科室ID查询所有关联记录
     */
    List<ExaminationItemDepartment> findByDepartmentId(String departmentId);
}

