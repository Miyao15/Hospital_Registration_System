package com.hospital.registration.service;

import com.hospital.registration.dto.request.UpdateDoctorProfileRequest;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public Page<User> listUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
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
}
