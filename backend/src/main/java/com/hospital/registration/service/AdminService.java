package com.hospital.registration.service;

import com.hospital.registration.dto.UserDetailDTO;
import com.hospital.registration.dto.request.UpdateDoctorProfileRequest;
import com.hospital.registration.dto.request.UpdatePatientInfoRequest;
import com.hospital.registration.entity.Admin;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hospital.registration.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDetailDTO> listUsers(int page, int size) {
        try {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
            List<UserDetailDTO> dtoList = userPage.getContent().stream()
                    .map(this::convertToUserDetailDTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(dtoList, userPage.getPageable(), userPage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户列表失败: " + e.getMessage(), e);
        }
    }

    private UserDetailDTO convertToUserDetailDTO(User user) {
        try {
            UserDetailDTO dto = new UserDetailDTO();
            dto.setId(user.getId());
            dto.setPhone(user.getPhone());
            dto.setRole(user.getRole());
            dto.setStatus(user.getStatus());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setLastLoginAt(user.getLastLoginAt());
            dto.setLoginFailures(user.getLoginFailures());
            dto.setLockedUntil(user.getLockedUntil());

            // 根据角色获取姓名
            String realName = null;
            try {
                switch (user.getRole()) {
                    case PATIENT:
                        realName = patientRepository.findByUserId(user.getId())
                                .map(Patient::getName)
                                .orElse(null);
                        break;
                    case DOCTOR:
                        realName = doctorRepository.findByUserId(user.getId())
                                .map(Doctor::getName)
                                .orElse(null);
                        break;
                    case ADMIN:
                        realName = adminRepository.findByUserId(user.getId())
                                .map(Admin::getName)
                                .orElse(null);
                        break;
                }
            } catch (Exception e) {
                // 如果查询姓名失败，记录日志但不抛出异常
                System.err.println("获取用户姓名失败 - userId: " + user.getId() + ", role: " + user.getRole() + ", error: " + e.getMessage());
                e.printStackTrace();
            }
            dto.setRealName(realName);

            return dto;
        } catch (Exception e) {
            System.err.println("转换UserDetailDTO失败 - userId: " + (user != null ? user.getId() : "null") + ", error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void approveDoctor(String doctorId, String adminId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> BusinessException.notFound("医师"));

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> BusinessException.notFound("用户"));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "该医师账户不是待审批状态", 
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        // 更新用户状态
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 记录审批信息
        doctor.setApprovedAt(LocalDateTime.now());
        doctor.setApprovedBy(adminId);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void toggleUserStatus(String userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户"));

        user.setStatus(status);
        if (status == UserStatus.ACTIVE) {
            user.setLockedUntil(null);
            user.setLoginFailures(0);
        }
        userRepository.save(user);
    }

    @Transactional
    public void updateDoctorProfile(String doctorId, UpdateDoctorProfileRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> BusinessException.notFound("医师"));

        Optional.ofNullable(request.getName()).ifPresent(doctor::setName);
        Optional.ofNullable(request.getEmployeeId()).ifPresent(doctor::setEmployeeId);
        Optional.ofNullable(request.getDepartmentId()).ifPresent(doctor::setDepartmentId);
        Optional.ofNullable(request.getSpecialty()).ifPresent(doctor::setSpecialty);
        Optional.ofNullable(request.getIntroduction()).ifPresent(doctor::setIntroduction);
        Optional.ofNullable(request.getAvatarUrl()).ifPresent(doctor::setAvatarUrl);
        Optional.ofNullable(request.getEducation()).ifPresent(doctor::setEducation);
        Optional.ofNullable(request.getScheduleInfo()).ifPresent(doctor::setScheduleInfo);

        doctorRepository.save(doctor);
    }

    // 修改患者信息
    @Transactional
    public void updatePatientInfo(String userId, UpdatePatientInfoRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("患者"));

        Optional.ofNullable(request.getName()).ifPresent(patient::setName);
        Optional.ofNullable(request.getGender()).ifPresent(patient::setGender);
        Optional.ofNullable(request.getBirthDate()).ifPresent(patient::setBirthDate);
        Optional.ofNullable(request.getMedicalHistory()).ifPresent(patient::setMedicalHistory);
        Optional.ofNullable(request.getAllergyHistory()).ifPresent(patient::setAllergyHistory);
        Optional.ofNullable(request.getEmergencyContact()).ifPresent(patient::setEmergencyContact);
        Optional.ofNullable(request.getEmergencyPhone()).ifPresent(patient::setEmergencyPhone);

        patientRepository.save(patient);
    }

    // 管理员修改用户密码
    @Transactional
    public void adminChangeUserPassword(String userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户"));

        // 验证新密码强度
        if (!ValidationUtils.isValidPassword(newPassword)) {
            throw BusinessException.weakPassword();
        }

        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
