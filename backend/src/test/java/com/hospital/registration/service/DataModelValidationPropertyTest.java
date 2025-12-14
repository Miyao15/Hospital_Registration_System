package com.hospital.registration.service;

import com.hospital.registration.dto.request.DoctorRegistrationRequest;
import com.hospital.registration.dto.request.PatientRegistrationRequest;
import com.hospital.registration.enums.DoctorTitle;
import com.hospital.registration.enums.Gender;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.util.ValidationUtils;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 属性测试：数据模型验证
 * 
 * **Feature: user-authentication, Property 3: 无效输入验证**
 * **Validates: Requirements 1.3, 1.4, 2.3**
 * 
 * 对于任意包含无效格式数据（无效手机号、无效身份证格式、无效资格证号）的注册请求，
 * 系统应拒绝请求并返回验证错误。
 */
class DataModelValidationPropertyTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private DoctorRepository doctorRepository;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // ========== 属性 3: 无效输入验证 ==========

    /**
     * 属性测试：无效手机号应被拒绝
     * **Feature: user-authentication, Property 3: 无效输入验证**
     * **Validates: Requirements 1.3**
     */
    @Property(tries = 100)
    void invalidPhoneShouldBeRejected(@ForAll("invalidPhones") String phone) {
        // 验证无效手机号格式
        assertFalse(ValidationUtils.isValidPhone(phone),
                "无效手机号应该被拒绝: " + phone);
    }

    @Provide
    Arbitrary<String> invalidPhones() {
        return Arbitraries.oneOf(
                // 空字符串
                Arbitraries.just(""),
                // 太短
                Arbitraries.strings().withCharRange('0', '9').ofMinLength(1).ofMaxLength(10),
                // 太长
                Arbitraries.strings().withCharRange('0', '9').ofMinLength(12).ofMaxLength(15),
                // 不以1开头
                Arbitraries.strings().withCharRange('0', '9').ofLength(11)
                        .filter(s -> !s.startsWith("1")),
                // 第二位不在3-9范围
                Arbitraries.integers().between(0, 2)
                        .map(i -> "1" + i + "123456789")
        );
    }

    /**
     * 属性测试：无效身份证号应被拒绝
     * **Feature: user-authentication, Property 3: 无效输入验证**
     * **Validates: Requirements 1.4**
     */
    @Property(tries = 100)
    void invalidIdCardShouldBeRejected(@ForAll("invalidIdCards") String idCard) {
        // 验证无效身份证格式
        assertFalse(ValidationUtils.isValidIdCard(idCard),
                "无效身份证应该被拒绝: " + idCard);
    }

    @Provide
    Arbitrary<String> invalidIdCards() {
        return Arbitraries.oneOf(
                // 空字符串
                Arbitraries.just(""),
                // 太短（少于18位）
                Arbitraries.strings().withCharRange('0', '9').ofMinLength(1).ofMaxLength(17),
                // 太长（超过18位）
                Arbitraries.strings().withCharRange('0', '9').ofMinLength(19).ofMaxLength(25),
                // 包含非法字符
                Arbitraries.strings().alpha().ofLength(18),
                // 校验码错误（生成17位数字+确保错误的校验码）
                generateIdCardWithWrongChecksum()
        );
    }

    /**
     * 生成校验码错误的身份证号
     */
    private Arbitrary<String> generateIdCardWithWrongChecksum() {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        return Arbitraries.strings().withCharRange('0', '9').ofLength(17)
                .map(base -> {
                    // 计算正确的校验码
                    int sum = 0;
                    for (int i = 0; i < 17; i++) {
                        sum += (base.charAt(i) - '0') * weights[i];
                    }
                    char correctCheckCode = checkCodes[sum % 11];
                    
                    // 选择一个错误的校验码
                    char wrongCheckCode;
                    if (correctCheckCode == 'X') {
                        wrongCheckCode = '0';
                    } else if (correctCheckCode == '0') {
                        wrongCheckCode = '1';
                    } else {
                        wrongCheckCode = (char) (correctCheckCode - 1);
                    }
                    
                    return base + wrongCheckCode;
                });
    }

    /**
     * 属性测试：无效医师资格证号应被拒绝
     * **Feature: user-authentication, Property 3: 无效输入验证**
     * **Validates: Requirements 2.3**
     */
    @Property(tries = 100)
    void invalidLicenseNumberShouldBeRejected(@ForAll("invalidLicenseNumbers") String license) {
        // 验证无效资格证格式
        assertFalse(ValidationUtils.isValidLicenseNumber(license),
                "无效资格证号应该被拒绝: " + license);
    }

    @Provide
    Arbitrary<String> invalidLicenseNumbers() {
        return Arbitraries.oneOf(
                // 空字符串
                Arbitraries.just(""),
                // 太短（少于10位）
                Arbitraries.strings().withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
                        .ofMinLength(1).ofMaxLength(9),
                // 太长（超过20位）
                Arbitraries.strings().withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
                        .ofMinLength(21).ofMaxLength(30),
                // 包含特殊字符
                Arbitraries.strings().withChars("!@#$%^&*()").ofMinLength(10).ofMaxLength(20)
        );
    }

    /**
     * 属性测试：无效密码应被拒绝
     * **Feature: user-authentication, Property 3: 无效输入验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void invalidPasswordShouldBeRejected(@ForAll("invalidPasswords") String password) {
        // 验证无效密码
        assertFalse(ValidationUtils.isValidPassword(password),
                "无效密码应该被拒绝: " + password);
    }

    @Provide
    Arbitrary<String> invalidPasswords() {
        return Arbitraries.oneOf(
                // 空字符串
                Arbitraries.just(""),
                // 太短（少于8位）
                Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(7),
                // 只有字母
                Arbitraries.strings().alpha().ofMinLength(8).ofMaxLength(16),
                // 只有数字
                Arbitraries.strings().numeric().ofMinLength(8).ofMaxLength(16)
        );
    }

    // ========== 辅助方法 ==========

    private PatientRegistrationRequest createPatientRequest(String phone, String idCard, String password) {
        PatientRegistrationRequest request = new PatientRegistrationRequest();
        request.setPhone(phone);
        request.setPassword(password);
        request.setName("测试患者");
        request.setIdCard(idCard);
        request.setGender(Gender.MALE);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        return request;
    }

    private DoctorRegistrationRequest createDoctorRequest(String phone, String licenseNumber, String password) {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest();
        request.setPhone(phone);
        request.setPassword(password);
        request.setName("测试医师");
        request.setEmployeeId("EMP001");
        request.setTitle(DoctorTitle.ATTENDING);
        request.setDepartmentId("DEPT001");
        request.setLicenseNumber(licenseNumber);
        return request;
    }
}
