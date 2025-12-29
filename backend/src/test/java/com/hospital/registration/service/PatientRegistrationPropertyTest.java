package com.hospital.registration.service;

import com.hospital.registration.dto.request.PatientRegistrationRequest;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.Gender;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 属性测试：患者注册
 */
class PatientRegistrationPropertyTest {

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
        
        // 使用反射设置 @Value 字段
        setField(authService, "maxLoginFailures", 5);
        setField(authService, "lockDurationMinutes", 30);
        setField(authService, "verificationCodeTtl", 300);
    }
    
    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ========== 属性 1: 有效患者注册创建账户 ==========

    /**
     * **Feature: user-authentication, Property 1: 有效患者注册创建账户**
     * **Validates: Requirements 1.1**
     */
    @Property(tries = 100)
    void validPatientRegistrationShouldCreateActiveAccount(
            @ForAll("validPatientRequests") PatientRegistrationRequest request) {
        
        // 重置 mock 以避免累积调用
        Mockito.reset(userRepository, patientRepository);
        
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByIdCard(anyString())).thenReturn(false);
        
        String userId = authService.registerPatient(request);
        
        assertNotNull(userId);
        assertFalse(userId.isEmpty());
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertEquals(UserRole.PATIENT, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
        assertEquals(request.getPhone(), savedUser.getPhone());
        
        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();
        
        assertEquals(request.getName(), savedPatient.getName());
        assertEquals(request.getIdCard(), savedPatient.getIdCard());
    }

    @Provide
    Arbitrary<PatientRegistrationRequest> validPatientRequests() {
        return Combinators.combine(
                validPhones(),
                validPasswords(),
                validNames(),
                validIdCards(),
                Arbitraries.of(Gender.MALE, Gender.FEMALE),
                validBirthDates()
        ).as((phone, password, name, idCard, gender, birthDate) -> {
            PatientRegistrationRequest req = new PatientRegistrationRequest();
            req.setPhone(phone);
            req.setPassword(password);
            req.setName(name);
            req.setIdCard(idCard);
            req.setGender(gender);
            req.setBirthDate(birthDate);
            return req;
        });
    }

    // ========== 属性 2: 重复身份证拒绝 ==========

    /**
     * **Feature: user-authentication, Property 2: 重复身份证拒绝**
     * **Validates: Requirements 1.2**
     */
    @Property(tries = 100)
    void duplicateIdCardShouldBeRejected(
            @ForAll("validPatientRequests") PatientRegistrationRequest request) {
        
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByIdCard(anyString())).thenReturn(true);
        
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> authService.registerPatient(request));
        
        assertEquals("REG_005", exception.getCode());
    }

    // ========== 属性 4: 必填字段验证 ==========

    /**
     * **Feature: user-authentication, Property 4: 必填字段验证**
     * **Validates: Requirements 1.5**
     */
    @Property(tries = 50)
    void missingPhoneShouldBeRejected(@ForAll("requestsWithoutPhone") PatientRegistrationRequest request) {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.registerPatient(request));
        
        assertEquals("REG_001", exception.getCode());
    }

    @Provide
    Arbitrary<PatientRegistrationRequest> requestsWithoutPhone() {
        return Combinators.combine(
                validPasswords(),
                validNames(),
                validIdCards(),
                Arbitraries.of(Gender.MALE, Gender.FEMALE),
                validBirthDates()
        ).as((password, name, idCard, gender, birthDate) -> {
            PatientRegistrationRequest req = new PatientRegistrationRequest();
            req.setPhone(null);
            req.setPassword(password);
            req.setName(name);
            req.setIdCard(idCard);
            req.setGender(gender);
            req.setBirthDate(birthDate);
            return req;
        });
    }

    /**
     * **Feature: user-authentication, Property 4: 必填字段验证**
     * **Validates: Requirements 1.5**
     */
    @Property(tries = 50)
    void missingIdCardShouldBeRejected(@ForAll("requestsWithoutIdCard") PatientRegistrationRequest request) {
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.registerPatient(request));
        
        assertEquals("REG_002", exception.getCode());
    }

    @Provide
    Arbitrary<PatientRegistrationRequest> requestsWithoutIdCard() {
        return Combinators.combine(
                validPhones(),
                validPasswords(),
                validNames(),
                Arbitraries.of(Gender.MALE, Gender.FEMALE),
                validBirthDates()
        ).as((phone, password, name, gender, birthDate) -> {
            PatientRegistrationRequest req = new PatientRegistrationRequest();
            req.setPhone(phone);
            req.setPassword(password);
            req.setName(name);
            req.setIdCard(null);
            req.setGender(gender);
            req.setBirthDate(birthDate);
            return req;
        });
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

    private Arbitrary<String> validIdCards() {
        String[] regions = {"110101", "110102", "310101", "440101", "510101"};
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        return Arbitraries.of(regions).flatMap(region ->
            Arbitraries.integers().between(1950, 2005).flatMap(year ->
                Arbitraries.integers().between(1, 12).flatMap(month ->
                    Arbitraries.integers().between(1, 28).flatMap(day ->
                        Arbitraries.integers().between(1, 999).map(seq -> {
                            String base = String.format("%s%04d%02d%02d%03d", 
                                    region, year, month, day, seq);
                            int sum = 0;
                            for (int i = 0; i < 17; i++) {
                                sum += (base.charAt(i) - '0') * weights[i];
                            }
                            return base + checkCodes[sum % 11];
                        })
                    )
                )
            )
        );
    }

    private Arbitrary<LocalDate> validBirthDates() {
        return Arbitraries.integers().between(1950, 2005).flatMap(year ->
            Arbitraries.integers().between(1, 12).flatMap(month ->
                Arbitraries.integers().between(1, 28).map(day ->
                    LocalDate.of(year, month, day)
                )
            )
        );
    }
}
