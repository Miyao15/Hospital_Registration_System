package com.hospital.registration.service;

import com.hospital.registration.dto.PatientProfileDTO;
import com.hospital.registration.dto.UpdatePatientProfileDTO;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientProfileDTO getPatientProfile(String userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        return convertToDTO(patient);
    }

    @Transactional
    public PatientProfileDTO updatePatientProfile(String userId, UpdatePatientProfileDTO dto) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));

        if (dto.getName() != null) {
            patient.setName(dto.getName());
        }
        if (dto.getIdCard() != null) {
            patient.setIdCard(dto.getIdCard());
        }
        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }
        if (dto.getBirthDate() != null) {
            patient.setBirthDate(dto.getBirthDate());
        }
        if (dto.getMedicalHistory() != null) {
            patient.setMedicalHistory(dto.getMedicalHistory());
        }
        if (dto.getAllergyHistory() != null) {
            patient.setAllergyHistory(dto.getAllergyHistory());
        }
        if (dto.getEmergencyContact() != null) {
            patient.setEmergencyContact(dto.getEmergencyContact());
        }
        if (dto.getEmergencyPhone() != null) {
            patient.setEmergencyPhone(dto.getEmergencyPhone());
        }

        patient = patientRepository.save(patient);
        log.info("更新患者信息成功 - patientId: {}", patient.getId());
        return convertToDTO(patient);
    }

    private PatientProfileDTO convertToDTO(Patient patient) {
        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setIdCard(patient.getIdCard());
        dto.setGender(patient.getGender());
        dto.setBirthDate(patient.getBirthDate());
        dto.setMedicalHistory(patient.getMedicalHistory());
        dto.setAllergyHistory(patient.getAllergyHistory());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        return dto;
    }
}

