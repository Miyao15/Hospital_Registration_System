package com.hospital.registration.controller;

import com.hospital.registration.dto.DepartmentDTO;
import com.hospital.registration.dto.DepartmentListDTO;
import com.hospital.registration.dto.DoctorListDTO;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.service.DepartmentService;
import com.hospital.registration.service.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    private final DoctorProfileService doctorProfileService;
    
    @GetMapping
    public ApiResponse<List<DepartmentDTO>> getAllDepartments() {
        return ApiResponse.success(departmentService.getAllDepartments());
    }
    
    @GetMapping("/grouped")
    public ApiResponse<List<DepartmentListDTO>> getDepartmentsGroupedByCategory() {
        return ApiResponse.success(departmentService.getAllDepartmentsGroupedByCategory());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<DepartmentDTO> getDepartmentById(@PathVariable String id) {
        return ApiResponse.success(departmentService.getDepartmentById(id));
    }
    
    @GetMapping("/{id}/doctors")
    public ApiResponse<List<DoctorListDTO>> getDoctorsByDepartment(@PathVariable String id) {
        return ApiResponse.success(doctorProfileService.getDoctorsByDepartment(id));
    }
    
    @GetMapping("/category/{category}")
    public ApiResponse<List<DepartmentDTO>> getDepartmentsByCategory(@PathVariable String category) {
        return ApiResponse.success(departmentService.getDepartmentsByCategory(category));
    }

    @GetMapping("/test-encoding")
    public ApiResponse<String> testEncoding() {
        // This is a test endpoint to check character encoding.
        // It returns a hardcoded Chinese string.
        // If this string displays correctly in the browser,
        // it proves the web layer (Spring/Tomcat/Frontend) is fine,
        // and the issue is with the data in the database.
        return ApiResponse.success("编码测试：如果这句话正常显示，说明Web层没有问题。");
    }
}
