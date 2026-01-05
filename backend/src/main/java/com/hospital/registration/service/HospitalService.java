package com.hospital.registration.service;

import com.hospital.registration.dto.HospitalDTO;
import com.hospital.registration.entity.Hospital;
import com.hospital.registration.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    
    public List<HospitalDTO> getAllHospitals() {
        return hospitalRepository.findByEnabledTrueOrderByCityAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsByRegion(String region) {
        return hospitalRepository.findByRegionAndEnabledTrue(region).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsByCity(String city) {
        return hospitalRepository.findByCityAndEnabledTrue(city).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsByProvince(String province) {
        return hospitalRepository.findByProvinceAndEnabledTrue(province).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsByRegionAndCity(String region, String city) {
        return hospitalRepository.findByRegionAndCityAndEnabledTrue(region, city).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<HospitalDTO> getHospitalById(String id) {
        return hospitalRepository.findById(id).map(this::toDTO);
    }
    
    public HospitalDTO createHospital(HospitalDTO dto) {
        Hospital hospital = new Hospital();
        hospital.setId(UUID.randomUUID().toString());
        hospital.setName(dto.getName());
        hospital.setAddress(dto.getAddress());
        hospital.setProvince(dto.getProvince());
        hospital.setCity(dto.getCity());
        hospital.setDistrict(dto.getDistrict());
        hospital.setPostalCode(dto.getPostalCode());
        hospital.setRegion(dto.getRegion());
        hospital.setPhone(dto.getPhone());
        hospital.setLongitude(dto.getLongitude());
        hospital.setLatitude(dto.getLatitude());
        hospital.setDescription(dto.getDescription());
        hospital.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        
        return toDTO(hospitalRepository.save(hospital));
    }
    
    public HospitalDTO toDTO(Hospital hospital) {
        HospitalDTO dto = new HospitalDTO();
        dto.setId(hospital.getId());
        dto.setName(hospital.getName());
        dto.setAddress(hospital.getAddress());
        dto.setProvince(hospital.getProvince());
        dto.setCity(hospital.getCity());
        dto.setDistrict(hospital.getDistrict());
        dto.setPostalCode(hospital.getPostalCode());
        dto.setRegion(hospital.getRegion());
        dto.setPhone(hospital.getPhone());
        dto.setLongitude(hospital.getLongitude());
        dto.setLatitude(hospital.getLatitude());
        dto.setDescription(hospital.getDescription());
        dto.setEnabled(hospital.getEnabled());
        return dto;
    }
}
