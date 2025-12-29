package com.hospital.registration.service;

import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.security.JwtService;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 属性测试：密码重置
 */
class PasswordResetPropertyTest {

    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private AdminRepository adminRepository;
    private DepartmentRepository departmentRepository;
    private JwtService jwtService;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeProperty
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        adminRepository = Mockito.mock(AdminRepository.class);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        jwtService = Mockito.mock(JwtService.class);
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        passwordEncoder = new BCryptPasswordEncoder();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        authService = new AuthService(
                userRepository,
                patientRepository,
                doctorRepository,
                adminRepository,
                departmentRepository,
                passwordEncoder,
                jwtService,
                redisTemplate
        );

        setField(authService, "maxLoginFailures", 5);
        setField(authService, "lockDurationMinutes", 30);
        setField(authService, "verificationCodeTtl", 300);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ========== 属性 16: 验证码有效性 ==========

    /**
     * **Feature: user-authentication, Property 16: 验证码有效性**
     * **Validates: Requirements 6.1, 6.2, 6.3**
     * 
     * 正确的验证码应该被接受
     */
    @Property(tries = 100)
    void correctVerificationCodeShouldBeAccepted(
            @ForAll("validPhones") String phone,
            @ForAll("validCodes") String code) {

        Mockito.reset(redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("reset:" + phone)).thenReturn(code);

        boolean result = authService.verifyResetCode(phone, code);

        assertTrue(result, "正确的验证码应该被接受");
    }

    /**
     * **Feature: user-authentication, Property 16: 验证码有效性**
     * **Validates: Requirements 6.1, 6.2, 6.3**
     * 
     * 错误的验证码应该被拒绝
     */
    @Property(tries = 100)
    void wrongVerificationCodeShouldBeRejected(
            @ForAll("validPhones") String phone,
            @ForAll("validCodes") String correctCode,
            @ForAll("validCodes") String wrongCode) {

        Assume.that(!correctCode.equals(wrongCode));

        Mockito.reset(redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("reset:" + phone)).thenReturn(correctCode);

        boolean result = authService.verifyResetCode(phone, wrongCode);

        assertFalse(result, "错误的验证码应该被拒绝");
    }

    /**
     * 过期/不存在的验证码应该被拒绝
     */
    @Property(tries = 50)
    void expiredOrNonExistentCodeShouldBeRejected(
            @ForAll("validPhones") String phone,
            @ForAll("validCodes") String code) {

        Mockito.reset(redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("reset:" + phone)).thenReturn(null);

        boolean result = authService.verifyResetCode(phone, code);

        assertFalse(result, "过期或不存在的验证码应该被拒绝");
    }

    // ========== 属性 17: 密码重置使会话失效 ==========

    /**
     * **Feature: user-authentication, Property 17: 密码重置使会话失效**
     * **Validates: Requirements 6.4**
     * 
     * 密码重置成功后应该使会话失效
     */
    @Property(tries = 50)
    void passwordResetShouldInvalidateSessions(
            @ForAll("validPhones") String phone,
            @ForAll("validCodes") String code,
            @ForAll("validPasswords") String newPassword) {

        Mockito.reset(userRepository, redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String userId = UUID.randomUUID().toString();
        User user = createUser(userId, phone);

        when(valueOperations.get("reset:" + phone)).thenReturn(code);
        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(user));

        authService.resetPassword(phone, code, newPassword);

        // 验证会话失效标记被设置
        verify(valueOperations).set(
                eq("invalidate:" + userId),
                anyString(),
                eq(7L),
                eq(TimeUnit.DAYS)
        );

        // 验证验证码被删除
        verify(redisTemplate).delete("reset:" + phone);
    }

    /**
     * 无效验证码的密码重置应该失败
     */
    @Property(tries = 50)
    void passwordResetWithInvalidCodeShouldFail(
            @ForAll("validPhones") String phone,
            @ForAll("validCodes") String wrongCode,
            @ForAll("validPasswords") String newPassword) {

        Mockito.reset(redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("reset:" + phone)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.resetPassword(phone, wrongCode, newPassword));

        assertEquals("PWD_003", exception.getCode());
    }

    // ========== 辅助方法 ==========

    private User createUser(String userId, String phone) {
        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode("oldPassword123"));
        return user;
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<String> validPhones() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(9)
                .map(suffix -> "1" + Arbitraries.of("3", "4", "5", "6", "7", "8", "9").sample() + suffix);
    }

    @Provide
    Arbitrary<String> validCodes() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(6);
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
}
