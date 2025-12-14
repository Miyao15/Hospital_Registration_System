package com.hospital.registration.service;

import com.hospital.registration.dto.response.UserProfileResponse;
import com.hospital.registration.entity.Admin;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.Gender;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 属性测试：用户资料管理
 */
class UserProfilePropertyTest {

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

    // ========== 属性 10: 资料获取返回角色对应数据 ==========

    /**
     * **Feature: user-authentication, Property 10: 资料获取返回角色对应数据**
     * **Validates: Requirements 4.1**
     */
    @Property(tries = 100)
    void patientProfileShouldReturnPatientData(
            @ForAll("validUserIds") String userId,
            @ForAll("validPhones") String phone,
            @ForAll("validNames") String name) {

        Mockito.reset(userRepository, patientRepository);

        User user = createUser(userId, phone, UserRole.PATIENT, UserStatus.ACTIVE);
        Patient patient = createPatient(userId, name);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(userId)).thenReturn(Optional.of(patient));

        UserProfileResponse response = userService.getProfile(userId);

        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals(phone, response.getPhone());
        assertEquals(UserRole.PATIENT, response.getRole());
        assertNotNull(response.getProfile());
        assertTrue(response.getProfile() instanceof Patient);
    }

    /**
     * **Feature: user-authentication, Property 10: 资料获取返回角色对应数据**
     * **Validates: Requirements 4.1**
     */
    @Property(tries = 100)
    void doctorProfileShouldReturnDoctorData(
            @ForAll("validUserIds") String userId,
            @ForAll("validPhones") String phone,
            @ForAll("validNames") String name) {

        Mockito.reset(userRepository, doctorRepository);

        User user = createUser(userId, phone, UserRole.DOCTOR, UserStatus.ACTIVE);
        Doctor doctor = createDoctor(userId, name);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));

        UserProfileResponse response = userService.getProfile(userId);

        assertNotNull(response);
        assertEquals(UserRole.DOCTOR, response.getRole());
        assertTrue(response.getProfile() instanceof Doctor);
    }

    // ========== 属性 12: 受保护字段不可修改 ==========

    /**
     * **Feature: user-authentication, Property 12: 受保护字段不可修改**
     * **Validates: Requirements 4.3**
     */
    @Property(tries = 50)
    void patientProtectedFieldsShouldNotBeModifiable(
            @ForAll("validUserIds") String userId,
            @ForAll("patientProtectedFields") String protectedField) {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(protectedField, "new_value");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateProfile(userId, UserRole.PATIENT, updateData));

        assertEquals("AUTH_007", exception.getCode());
    }

    /**
     * **Feature: user-authentication, Property 12: 受保护字段不可修改**
     * **Validates: Requirements 4.3**
     */
    @Property(tries = 50)
    void doctorProtectedFieldsShouldNotBeModifiable(
            @ForAll("validUserIds") String userId,
            @ForAll("doctorProtectedFields") String protectedField) {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(protectedField, "new_value");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateProfile(userId, UserRole.DOCTOR, updateData));

        assertEquals("AUTH_007", exception.getCode());
    }

    // ========== 属性 11: 资料更新持久化 ==========

    /**
     * **Feature: user-authentication, Property 11: 资料更新持久化**
     * **Validates: Requirements 4.2**
     */
    @Property(tries = 50)
    void patientProfileUpdateShouldPersist(
            @ForAll("validUserIds") String userId,
            @ForAll("validPhones") String phone,
            @ForAll("validNames") String originalName,
            @ForAll("validNames") String newName) {

        Assume.that(!originalName.equals(newName));
        Mockito.reset(userRepository, patientRepository);

        User user = createUser(userId, phone, UserRole.PATIENT, UserStatus.ACTIVE);
        Patient patient = createPatient(userId, originalName);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(userId)).thenReturn(Optional.of(patient));

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", newName);

        userService.updateProfile(userId, UserRole.PATIENT, updateData);

        verify(patientRepository).save(argThat(p -> newName.equals(p.getName())));
    }

    // ========== 辅助方法 ==========

    private User createUser(String userId, String phone, UserRole role, UserStatus status) {
        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(status);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        return user;
    }

    private Patient createPatient(String userId, String name) {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setUserId(userId);
        patient.setName(name);
        patient.setIdCard("110101199001011234");
        patient.setGender(Gender.MALE);
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        return patient;
    }

    private Doctor createDoctor(String userId, String name) {
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID().toString());
        doctor.setUserId(userId);
        doctor.setName(name);
        doctor.setEmployeeId("D001");
        doctor.setTitle(DoctorTitle.ATTENDING);
        doctor.setDepartmentId("DEPT001");
        doctor.setLicenseNumber("ABC1234567890");
        return doctor;
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<String> validUserIds() {
        return Arbitraries.strings()
                .withCharRange('a', 'f')
                .withCharRange('0', '9')
                .ofLength(32)
                .map(s -> s.substring(0, 8) + "-" + s.substring(8, 12) + "-" +
                        s.substring(12, 16) + "-" + s.substring(16, 20) + "-" + s.substring(20));
    }

    @Provide
    Arbitrary<String> validPhones() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(9)
                .map(suffix -> "1" + Arbitraries.of("3", "4", "5", "6", "7", "8", "9").sample() + suffix);
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withChars("张王李赵刘陈杨黄周吴徐孙马朱胡林郭何高罗")
                .ofMinLength(2).ofMaxLength(4);
    }

    @Provide
    Arbitrary<String> patientProtectedFields() {
        return Arbitraries.of("idCard", "userId", "id");
    }

    @Provide
    Arbitrary<String> doctorProtectedFields() {
        return Arbitraries.of("employeeId", "userId", "id", "licenseNumber", "approvedAt", "approvedBy");
    }
}
