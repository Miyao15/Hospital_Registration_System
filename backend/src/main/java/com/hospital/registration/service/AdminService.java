package com.hospital.registration.service;

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
}
