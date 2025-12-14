package com.hospital.registration.service;

import com.hospital.registration.dto.response.UserProfileResponse;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // 受保护字段（不允许修改）
    private static final List<String> PROTECTED_PATIENT_FIELDS = Arrays.asList("idCard", "userId", "id");
    private static final List<String> PROTECTED_DOCTOR_FIELDS = Arrays.asList("employeeId", "userId", "id", "licenseNumber", "approvedAt", "approvedBy");

    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户"));

        Object profile = null;
        switch (user.getRole()) {
            case PATIENT:
                profile = patientRepository.findByUserId(userId).orElse(null);
                break;
            case DOCTOR:
                profile = doctorRepository.findByUserId(userId).orElse(null);
                break;
            case ADMIN:
                profile = adminRepository.findByUserId(userId).orElse(null);
                break;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .profile(profile)
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(String userId, UserRole role, Map<String, Object> updateData) {
        // 检查是否尝试修改受保护字段
        List<String> protectedFields = role == UserRole.PATIENT ? PROTECTED_PATIENT_FIELDS : PROTECTED_DOCTOR_FIELDS;
        
        for (String field : updateData.keySet()) {
            if (protectedFields.contains(field)) {
                throw BusinessException.accessDenied();
            }
        }

        switch (role) {
            case PATIENT:
                patientRepository.findByUserId(userId).ifPresent(patient -> {
                    if (updateData.containsKey("name")) patient.setName((String) updateData.get("name"));
                    if (updateData.containsKey("medicalHistory")) patient.setMedicalHistory((String) updateData.get("medicalHistory"));
                    if (updateData.containsKey("allergyHistory")) patient.setAllergyHistory((String) updateData.get("allergyHistory"));
                    if (updateData.containsKey("emergencyContact")) patient.setEmergencyContact((String) updateData.get("emergencyContact"));
                    if (updateData.containsKey("emergencyPhone")) patient.setEmergencyPhone((String) updateData.get("emergencyPhone"));
                    patientRepository.save(patient);
                });
                break;
            case DOCTOR:
                doctorRepository.findByUserId(userId).ifPresent(doctor -> {
                    if (updateData.containsKey("name")) doctor.setName((String) updateData.get("name"));
                    if (updateData.containsKey("specialty")) doctor.setSpecialty((String) updateData.get("specialty"));
                    if (updateData.containsKey("introduction")) doctor.setIntroduction((String) updateData.get("introduction"));
                    if (updateData.containsKey("avatarUrl")) doctor.setAvatarUrl((String) updateData.get("avatarUrl"));
                    doctorRepository.save(doctor);
                });
                break;
            case ADMIN:
                adminRepository.findByUserId(userId).ifPresent(admin -> {
                    if (updateData.containsKey("name")) admin.setName((String) updateData.get("name"));
                    adminRepository.save(admin);
                });
                break;
        }

        return getProfile(userId);
    }

    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户"));

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw BusinessException.wrongPassword();
        }

        // 验证新密码强度
        if (!ValidationUtils.isValidPassword(newPassword)) {
            throw BusinessException.weakPassword();
        }

        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
