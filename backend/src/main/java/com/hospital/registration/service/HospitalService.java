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
        return hospitalRepository.findByEnabledTrue().stream()
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
        hospital.setPhone(dto.getPhone());
        hospital.setLongitude(dto.getLongitude());
        hospital.setLatitude(dto.getLatitude());
        hospital.setDescription(dto.getDescription());
        hospital.setEnabled(true);
        
        return toDTO(hospitalRepository.save(hospital));
    }
    
    public HospitalDTO toDTO(Hospital hospital) {
        return new HospitalDTO(
            hospital.getId(),
            hospital.getName(),
            hospital.getAddress(),
            hospital.getPhone(),
            hospital.getLongitude(),
            hospital.getLatitude(),
            hospital.getDescription()
        );
    }
}
