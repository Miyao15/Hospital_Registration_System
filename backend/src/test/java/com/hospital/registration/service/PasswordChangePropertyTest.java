package com.hospital.registration.service;

import com.hospital.registration.entity.User;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 属性测试：密码修改
 * 
 * **Feature: user-authentication, Property 13: 密码修改往返测试**
 * **Validates: Requirements 4.4**
 */
class PasswordChangePropertyTest {

    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeProperty
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        adminRepository = Mockito.mock(AdminRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(
                userRepository,
                patientRepository,
                doctorRepository,
                adminRepository,
                passwordEncoder
        );
    }

    // ========== 属性 13: 密码修改往返测试 ==========

    /**
     * **Feature: user-authentication, Property 13: 密码修改往返测试**
     * **Validates: Requirements 4.4**
     * 
     * 对于任意成功的密码修改，新密码应该被正确加密存储
     */
    @Property(tries = 100)
    void passwordChangeShouldEncryptAndStoreNewPassword(
            @ForAll("validUserIds") String userId,
            @ForAll("validPasswords") String currentPassword,
            @ForAll("validPasswords") String newPassword) {

        Assume.that(!currentPassword.equals(newPassword));
        Mockito.reset(userRepository);

        String currentHash = passwordEncoder.encode(currentPassword);
        User user = createUser(userId, currentHash);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.changePassword(userId, currentPassword, newPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // 验证新密码被正确加密
        assertTrue(passwordEncoder.matches(newPassword, savedUser.getPasswordHash()),
                "新密码应该能够通过验证");
        assertFalse(passwordEncoder.matches(currentPassword, savedUser.getPasswordHash()),
                "旧密码不应该能够通过验证");
    }

    /**
     * **Feature: user-authentication, Property 13: 密码修改往返测试**
     * **Validates: Requirements 4.4**
     * 
     * 错误的当前密码应该被拒绝
     */
    @Property(tries = 100)
    void wrongCurrentPasswordShouldBeRejected(
            @ForAll("validUserIds") String userId,
            @ForAll("validPasswords") String correctPassword,
            @ForAll("validPasswords") String wrongPassword,
            @ForAll("validPasswords") String newPassword) {

        Assume.that(!correctPassword.equals(wrongPassword));
        Mockito.reset(userRepository);

        String correctHash = passwordEncoder.encode(correctPassword);
        User user = createUser(userId, correctHash);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.changePassword(userId, wrongPassword, newPassword));

        assertEquals("PWD_002", exception.getCode());
        verify(userRepository, never()).save(any());
    }

    /**
     * 弱密码应该被拒绝
     */
    @Property(tries = 50)
    void weakNewPasswordShouldBeRejected(
            @ForAll("validUserIds") String userId,
            @ForAll("validPasswords") String currentPassword,
            @ForAll("weakPasswords") String weakNewPassword) {

        Mockito.reset(userRepository);

        String currentHash = passwordEncoder.encode(currentPassword);
        User user = createUser(userId, currentHash);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.changePassword(userId, currentPassword, weakNewPassword));

        assertEquals("PWD_001", exception.getCode());
        verify(userRepository, never()).save(any());
    }

    // ========== 辅助方法 ==========

    private User createUser(String userId, String passwordHash) {
        User user = new User();
        user.setId(userId);
        user.setPhone("13800138000");
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordHash(passwordHash);
        return user;
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<String> validUserIds() {
        return Arbitraries.strings()
                .withCharRange('a', 'f')
                .withCharRange('0', '9')
                .ofLength(32)
                .map(s -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<String> validPasswords() {
        Arbitrary<String> letters = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5).ofMaxLength(8);
        Arbitrary<String> digits = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(3).ofMaxLength(5);
        return Combinators.combine(letters, digits).as((l, d) -> l + d);
    }

    @Provide
    Arbitrary<String> weakPasswords() {
        return Arbitraries.oneOf(
                // 太短
                Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(7),
                // 只有字母
                Arbitraries.strings().alpha().ofMinLength(8).ofMaxLength(12),
                // 只有数字
                Arbitraries.strings().numeric().ofMinLength(8).ofMaxLength(12)
        );
    }
}
