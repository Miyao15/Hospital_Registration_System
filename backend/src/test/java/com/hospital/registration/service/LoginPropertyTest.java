package com.hospital.registration.service;

import com.hospital.registration.dto.request.LoginRequest;
import com.hospital.registration.dto.response.LoginResponse;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.security.JwtService;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 属性测试：用户登录
 */
class LoginPropertyTest {

    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private JwtService jwtService;
    private StringRedisTemplate redisTemplate;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeProperty
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        jwtService = Mockito.mock(JwtService.class);
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(
                userRepository,
                patientRepository,
                doctorRepository,
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

    // ========== 属性 7: 有效凭证返回包含正确角色的 JWT ==========

    /**
     * **Feature: user-authentication, Property 7: 有效凭证返回包含正确角色的 JWT**
     * **Validates: Requirements 3.1**
     */
    @Property(tries = 100)
    void validCredentialsShouldReturnJwtWithCorrectRole(
            @ForAll("validPhones") String phone,
            @ForAll("validPasswords") String password,
            @ForAll("validRoles") UserRole role) {

        Mockito.reset(userRepository, jwtService);

        String userId = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(password);

        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setLoginFailures(0);

        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(userId, role)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userId, role)).thenReturn("refresh-token");
        when(jwtService.getAccessExpiration()).thenReturn(7200000L);

        LoginRequest request = new LoginRequest();
        request.setIdentifier(phone);
        request.setPassword(password);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(role, response.getRole());

        verify(jwtService).generateAccessToken(userId, role);
        verify(jwtService).generateRefreshToken(userId, role);
    }

    // ========== 属性 8: 无效凭证拒绝 ==========

    /**
     * **Feature: user-authentication, Property 8: 无效凭证拒绝**
     * **Validates: Requirements 3.2**
     */
    @Property(tries = 100)
    void invalidPasswordShouldBeRejectedAndIncrementFailureCount(
            @ForAll("validPhones") String phone,
            @ForAll("validPasswords") String correctPassword,
            @ForAll("validPasswords") String wrongPassword) {

        Assume.that(!correctPassword.equals(wrongPassword));

        Mockito.reset(userRepository);

        String userId = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(correctPassword);

        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        user.setLoginFailures(0);

        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setIdentifier(phone);
        request.setPassword(wrongPassword);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("AUTH_001", exception.getCode());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(1, savedUser.getLoginFailures());
    }

    /**
     * **Feature: user-authentication, Property 8: 无效凭证拒绝**
     * **Validates: Requirements 3.2**
     */
    @Property(tries = 50)
    void nonExistentUserShouldBeRejected(
            @ForAll("validPhones") String phone,
            @ForAll("validPasswords") String password) {

        Mockito.reset(userRepository, doctorRepository);

        when(userRepository.findByPhone(phone)).thenReturn(Optional.empty());
        when(doctorRepository.findByEmployeeId(phone)).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setIdentifier(phone);
        request.setPassword(password);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("AUTH_001", exception.getCode());
    }

    /**
     * 待审批账户不能登录
     */
    @Property(tries = 50)
    void pendingAccountShouldNotLogin(
            @ForAll("validPhones") String phone,
            @ForAll("validPasswords") String password) {

        Mockito.reset(userRepository);

        String userId = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(password);

        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.DOCTOR);
        user.setStatus(UserStatus.PENDING);
        user.setLoginFailures(0);

        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setIdentifier(phone);
        request.setPassword(password);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("AUTH_005", exception.getCode());
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
    Arbitrary<UserRole> validRoles() {
        return Arbitraries.of(UserRole.values());
    }
}
