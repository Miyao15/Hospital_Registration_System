package com.hospital.registration.service;

import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 属性测试：医师审批
 * 
 * **Feature: user-authentication, Property 6: 医师审批激活账户**
 * **Validates: Requirements 2.4**
 */
class DoctorApprovalPropertyTest {

    private UserRepository userRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;
    private AdminService adminService;

    @BeforeProperty
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        adminRepository = Mockito.mock(AdminRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        adminService = new AdminService(userRepository, doctorRepository, patientRepository, adminRepository, passwordEncoder);
    }

    // ========== 属性 6: 医师审批激活账户 ==========

    /**
     * **Feature: user-authentication, Property 6: 医师审批激活账户**
     * **Validates: Requirements 2.4**
     * 
     * 对于任意待审批的医师账户，审批通过后状态应变为 active
     */
    @Property(tries = 100)
    void pendingDoctorApprovalShouldActivateAccount(
            @ForAll("validDoctorIds") String doctorId,
            @ForAll("validUserIds") String userId,
            @ForAll("validAdminIds") String adminId) {

        Mockito.reset(userRepository, doctorRepository);

        Doctor doctor = createDoctor(doctorId, userId);
        User user = createUser(userId, UserStatus.PENDING);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        adminService.approveDoctor(doctorId, adminId);

        // 验证用户状态变为 ACTIVE
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());

        // 验证审批信息被记录
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctor = doctorCaptor.getValue();
        assertNotNull(savedDoctor.getApprovedAt());
        assertEquals(adminId, savedDoctor.getApprovedBy());
    }

    /**
     * 非待审批状态的医师不能被审批
     */
    @Property(tries = 50)
    void nonPendingDoctorShouldNotBeApproved(
            @ForAll("validDoctorIds") String doctorId,
            @ForAll("validUserIds") String userId,
            @ForAll("validAdminIds") String adminId,
            @ForAll("nonPendingStatuses") UserStatus status) {

        Mockito.reset(userRepository, doctorRepository);

        Doctor doctor = createDoctor(doctorId, userId);
        User user = createUser(userId, status);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> adminService.approveDoctor(doctorId, adminId));

        assertEquals("INVALID_STATUS", exception.getCode());
        verify(userRepository, never()).save(any());
    }

    /**
     * 不存在的医师审批应该失败
     */
    @Property(tries = 50)
    void nonExistentDoctorApprovalShouldFail(
            @ForAll("validDoctorIds") String doctorId,
            @ForAll("validAdminIds") String adminId) {

        Mockito.reset(doctorRepository);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> adminService.approveDoctor(doctorId, adminId));

        assertEquals("NOT_FOUND", exception.getCode());
    }

    // ========== 辅助方法 ==========

    private Doctor createDoctor(String doctorId, String userId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setUserId(userId);
        doctor.setName("测试医师");
        doctor.setEmployeeId("D001");
        doctor.setTitle(DoctorTitle.ATTENDING);
        doctor.setDepartmentId("DEPT001");
        doctor.setLicenseNumber("ABC1234567890");
        return doctor;
    }

    private User createUser(String userId, UserStatus status) {
        User user = new User();
        user.setId(userId);
        user.setPhone("13800138000");
        user.setRole(UserRole.DOCTOR);
        user.setStatus(status);
        user.setPasswordHash("hashedPassword");
        return user;
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<String> validDoctorIds() {
        return Arbitraries.create(() -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<String> validUserIds() {
        return Arbitraries.create(() -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<String> validAdminIds() {
        return Arbitraries.create(() -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<UserStatus> nonPendingStatuses() {
        return Arbitraries.of(UserStatus.ACTIVE, UserStatus.LOCKED, UserStatus.DISABLED);
    }
}
