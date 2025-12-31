package com.hospital.registration.controller;

import com.hospital.registration.dto.HospitalDTO;
import com.hospital.registration.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    
    private final HospitalService hospitalService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHospitals() {
        List<HospitalDTO> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "success",
            "data", hospitals
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHospitalById(@PathVariable String id) {
        return hospitalService.getHospitalById(id)
            .map(hospital -> ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "success",
                "data", (Object) hospital
            )))
            .orElse(ResponseEntity.ok(Map.of(
                "code", 404,
                "message", "医院不存在"
            )));
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHospital(@RequestBody HospitalDTO dto) {
        HospitalDTO created = hospitalService.createHospital(dto);
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "创建成功",
            "data", created
        ));
    }
}
