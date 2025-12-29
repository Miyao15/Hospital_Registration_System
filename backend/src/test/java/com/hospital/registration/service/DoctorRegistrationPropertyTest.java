package com.hospital.registration.service;

import com.hospital.registration.dto.request.DoctorRegistrationRequest;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.DoctorTitle;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 属性测试：医师注册
 */
class DoctorRegistrationPropertyTest {

    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private AdminRepository adminRepository;
    private DepartmentRepository departmentRepository;
    private JwtService jwtService;
    private StringRedisTemplate redisTemplate;
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
        passwordEncoder = new BCryptPasswordEncoder();

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

    // ========== 属性 5: 医师注册创建待审批账户 ==========

    /**
     * **Feature: user-authentication, Property 5: 医师注册创建待审批账户**
     * **Validates: Requirements 2.1**
     */
    @Property(tries = 100)
    void validDoctorRegistrationShouldCreatePendingAccount(
            @ForAll("validDoctorRequests") DoctorRegistrationRequest request) {

        Mockito.reset(userRepository, doctorRepository);

        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(doctorRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyString())).thenReturn(true); // 模拟科室存在

        String userId = authService.registerDoctor(request);

        assertNotNull(userId);
        assertFalse(userId.isEmpty());

        // 验证用户创建
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(UserRole.DOCTOR, savedUser.getRole());
        assertEquals(UserStatus.PENDING, savedUser.getStatus());
        assertEquals(request.getPhone(), savedUser.getPhone());

        // 验证医师信息创建
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctor = doctorCaptor.getValue();

        assertEquals(request.getName(), savedDoctor.getName());
        assertEquals(request.getEmployeeId(), savedDoctor.getEmployeeId());
        assertEquals(request.getLicenseNumber(), savedDoctor.getLicenseNumber());
        assertEquals(request.getTitle(), savedDoctor.getTitle());
    }

    @Provide
    Arbitrary<DoctorRegistrationRequest> validDoctorRequests() {
        return Combinators.combine(
                validPhones(),
                validPasswords(),
                validNames(),
                validEmployeeIds(),
                Arbitraries.of(DoctorTitle.values()),
                validDepartmentIds(),
                validLicenseNumbers()
        ).as((phone, password, name, employeeId, title, departmentId, licenseNumber) -> {
            DoctorRegistrationRequest req = new DoctorRegistrationRequest();
            req.setPhone(phone);
            req.setPassword(password);
            req.setName(name);
            req.setEmployeeId(employeeId);
            req.setTitle(title);
            req.setDepartmentId(departmentId);
            req.setLicenseNumber(licenseNumber);
            req.setSpecialty("内科");
            req.setIntroduction("医师简介");
            return req;
        });
    }

    // ========== 属性: 重复工号拒绝 ==========

    /**
     * **Feature: user-authentication, Property: 重复工号拒绝**
     * **Validates: Requirements 2.2**
     */
    @Property(tries = 100)
    void duplicateEmployeeIdShouldBeRejected(
            @ForAll("validDoctorRequests") DoctorRegistrationRequest request) {

        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(doctorRepository.existsByEmployeeId(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.registerDoctor(request));

        assertEquals("REG_006", exception.getCode());
    }

    // ========== 辅助生成器 ==========

    private Arbitrary<String> validPhones() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(9)
                .map(suffix -> "1" + Arbitraries.of("3", "4", "5", "6", "7", "8", "9").sample() + suffix);
    }

    private Arbitrary<String> validPasswords() {
        Arbitrary<String> letters = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5).ofMaxLength(8);
        Arbitrary<String> digits = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(3).ofMaxLength(5);
        return Combinators.combine(letters, digits).as((l, d) -> l + d);
    }

    private Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withChars("张王李赵刘陈杨黄周吴徐孙马朱胡林郭何高罗")
                .ofMinLength(2).ofMaxLength(4);
    }

    private Arbitrary<String> validEmployeeIds() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(6).ofMaxLength(10)
                .map(s -> "D" + s);
    }

    private Arbitrary<String> validDepartmentIds() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(8)
                .map(s -> "DEPT" + s);
    }

    private Arbitrary<String> validLicenseNumbers() {
        Arbitrary<String> prefix = Arbitraries.strings()
                .withCharRange('A', 'Z')
                .ofMinLength(2).ofMaxLength(4);
        Arbitrary<String> suffix = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(8).ofMaxLength(14);
        return Combinators.combine(prefix, suffix).as((p, s) -> p + s);
    }
}
