package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.entity.MedicalItem;
import com.hospital.registration.service.MedicalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-items")
public class MedicalItemController {

    @Autowired
    private MedicalItemService medicalItemService;

    @GetMapping
    public ApiResponse<List<MedicalItem>> getAllMedicalItems() {
        return ApiResponse.success(medicalItemService.getAllMedicalItems());
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicalItem> getMedicalItemById(@PathVariable String id) {
        MedicalItem medicalItem = medicalItemService.getMedicalItemById(id);
        return ApiResponse.success(medicalItem);
    }

    @PostMapping
    public ApiResponse<MedicalItem> createMedicalItem(@RequestBody MedicalItem medicalItem) {
        return ApiResponse.success(medicalItemService.createMedicalItem(medicalItem));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicalItem> updateMedicalItem(@PathVariable String id, @RequestBody MedicalItem medicalItem) {
        MedicalItem updatedMedicalItem = medicalItemService.updateMedicalItem(id, medicalItem);
        return ApiResponse.success(updatedMedicalItem);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMedicalItem(@PathVariable String id) {
        medicalItemService.deleteMedicalItem(id);
        return ApiResponse.success(null);
    }
}
