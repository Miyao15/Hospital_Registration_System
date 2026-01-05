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
        log.info("获取患者个人资料 - userId: {}", userId);
        try {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        log.warn("找不到患者信息 - userId: {}", userId);
                        return new BusinessException("患者信息不存在，请先完善个人信息");
                    });
            log.info("找到患者信息 - patientId: {}, name: {}", patient.getId(), patient.getName());
            return convertToDTO(patient);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取患者个人资料失败 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new BusinessException("获取患者信息失败: " + e.getMessage());
        }
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
        if (dto.getAvatarUrl() != null) {
            patient.setAvatarUrl(dto.getAvatarUrl());
        }

        patient = patientRepository.save(patient);
        log.info("更新患者信息成功 - patientId: {}", patient.getId());
        return convertToDTO(patient);
    }

    private PatientProfileDTO convertToDTO(Patient patient) {
        if (patient == null) {
            log.error("患者对象为null");
            throw new BusinessException("患者数据不存在");
        }
        
        try {
            log.debug("开始转换患者信息DTO - patientId: {}", patient.getId());
            PatientProfileDTO dto = new PatientProfileDTO();
            dto.setId(patient.getId());
            dto.setName(patient.getName() != null ? patient.getName() : "");
            dto.setIdCard(patient.getIdCard());
            dto.setGender(patient.getGender());
            dto.setBirthDate(patient.getBirthDate());
            dto.setMedicalHistory(patient.getMedicalHistory());
            dto.setAllergyHistory(patient.getAllergyHistory());
            dto.setEmergencyContact(patient.getEmergencyContact());
            dto.setEmergencyPhone(patient.getEmergencyPhone());
            dto.setAvatarUrl(patient.getAvatarUrl());
            log.debug("患者信息DTO转换成功 - patientId: {}", patient.getId());
            return dto;
        } catch (Exception e) {
            log.error("转换患者信息DTO失败 - patientId: {}, error: {}", 
                    patient != null ? patient.getId() : "null", e.getMessage(), e);
            throw new BusinessException("获取患者信息失败: " + e.getMessage());
        }
    }
}

