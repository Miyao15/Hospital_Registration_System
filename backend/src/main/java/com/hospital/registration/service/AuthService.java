package com.hospital.registration.service;

import com.hospital.registration.dto.request.AdminRegistrationRequest;
import com.hospital.registration.dto.request.DoctorRegistrationRequest;
import com.hospital.registration.dto.request.LoginRequest;
import com.hospital.registration.dto.request.PatientRegistrationRequest;
import com.hospital.registration.dto.response.LoginResponse;
import com.hospital.registration.entity.Admin;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.User;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.AdminRepository;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.DepartmentRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.security.JwtService;
import com.hospital.registration.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.max-login-failures}")
    private int maxLoginFailures;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Value("${app.security.verification-code-ttl}")
    private int verificationCodeTtl;

    @Value("${app.security.admin-registration-key}")
    private String adminRegistrationKey;

    @Transactional
    public String registerAdmin(AdminRegistrationRequest request) {
        // 1. 验证管理员注册密钥
        if (!Objects.equals(adminRegistrationKey, request.getAdminRegistrationKey())) {
            throw BusinessException.invalidAdminKey();
        }

        // 2. 验证手机号格式
        if (!ValidationUtils.isValidPhone(request.getPhone())) {
            throw BusinessException.invalidPhone();
        }

        // 3. 验证密码强度
        if (!ValidationUtils.isValidPassword(request.getPassword())) {
            throw BusinessException.weakPassword();
        }

        // 4. 检查手机号是否已注册
        if (userRepository.existsByPhone(request.getPhone())) {
            throw BusinessException.phoneExists();
        }

        // 5. 检查工号是否已注册
        if (adminRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
            throw BusinessException.employeeIdExists();
        }

        // 6. 创建用户
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE); // 管理员账户直接激活
        userRepository.save(user);

        // 7. 创建管理员信息
        Admin admin = new Admin();
        admin.setId(UUID.randomUUID().toString());
        admin.setUserId(userId);
        admin.setName(request.getName());
        admin.setEmployeeId(request.getEmployeeId());
        adminRepository.save(admin);

        return userId;
    }

    @Transactional
    public String registerPatient(PatientRegistrationRequest request) {
        // 验证手机号格式
        if (!ValidationUtils.isValidPhone(request.getPhone())) {
            throw BusinessException.invalidPhone();
        }

        // 验证身份证格式
        if (!ValidationUtils.isValidIdCard(request.getIdCard())) {
            throw BusinessException.invalidIdCard();
        }

        // 验证密码强度
        if (!ValidationUtils.isValidPassword(request.getPassword())) {
            throw BusinessException.weakPassword();
        }

        // 检查手机号是否已注册
        if (userRepository.existsByPhone(request.getPhone())) {
            throw BusinessException.phoneExists();
        }

        // 检查身份证是否已注册
        if (patientRepository.existsByIdCard(request.getIdCard())) {
            throw BusinessException.idCardExists();
        }

        // 创建用户
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 创建患者信息
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setUserId(userId);
        patient.setName(request.getName());
        patient.setIdCard(request.getIdCard());
        patient.setGender(request.getGender());
        patient.setBirthDate(request.getBirthDate());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setAllergyHistory(request.getAllergyHistory());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        patientRepository.save(patient);

        return userId;
    }

    @Transactional
    public String registerDoctor(DoctorRegistrationRequest request) {
        // 验证手机号格式
        if (!ValidationUtils.isValidPhone(request.getPhone())) {
            throw BusinessException.invalidPhone();
        }

        // 验证资格证格式
        if (!ValidationUtils.isValidLicenseNumber(request.getLicenseNumber())) {
            throw BusinessException.invalidLicense();
        }

        // 验证密码强度
        if (!ValidationUtils.isValidPassword(request.getPassword())) {
            throw BusinessException.weakPassword();
        }

        // 检查手机号是否已注册
        if (userRepository.existsByPhone(request.getPhone())) {
            throw BusinessException.phoneExists();
        }

        // 检查工号是否已注册
        if (doctorRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw BusinessException.employeeIdExists();
        }

        // 检查医师资格证号是否已注册
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw BusinessException.licenseNumberExists();
        }

        // 检查科室是否存在
        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw BusinessException.departmentNotFound();
        }

        // 创建用户（状态为待审批）
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.DOCTOR);
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);

        // 创建医师信息
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID().toString());
        doctor.setUserId(userId);
        doctor.setName(request.getName());
        doctor.setEmployeeId(request.getEmployeeId());
        doctor.setTitle(request.getTitle());
        doctor.setDepartmentId(request.getDepartmentId());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setIntroduction(request.getIntroduction());
        doctorRepository.save(doctor);

        return userId;
    }

    public LoginResponse login(LoginRequest request) {
        // 查找用户（通过手机号、医生工号或管理员公号）
        User user = userRepository.findByPhone(request.getIdentifier())
                .orElseGet(() -> 
                    doctorRepository.findByEmployeeId(request.getIdentifier())
                        .map(doctor -> userRepository.findById(doctor.getUserId()).orElse(null))
                        .orElseGet(() -> 
                            adminRepository.findByEmployeeId(request.getIdentifier())
                                .map(admin -> userRepository.findById(admin.getUserId()).orElse(null))
                                .orElse(null)
                        )
                );

        if (user == null) {
            throw BusinessException.invalidCredentials();
        }

        // 检查账户状态
        checkAccountStatus(user);

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            recordLoginFailure(user);
            throw BusinessException.invalidCredentials();
        }

        // 清除登录失败记录
        clearLoginFailures(user);

        // 生成令牌
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpiration() / 1000)
                .role(user.getRole())
                .build();
    }

    private void checkAccountStatus(User user) {
        if (user.getStatus() == UserStatus.LOCKED) {
            if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
                // 解锁账户
                user.setStatus(UserStatus.ACTIVE);
                user.setLockedUntil(null);
                user.setLoginFailures(0);
                userRepository.save(user);
            } else {
                throw BusinessException.accountLocked();
            }
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw BusinessException.accountPending();
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw BusinessException.accountDisabled();
        }
    }

    private void recordLoginFailure(User user) {
        int failures = user.getLoginFailures() + 1;
        user.setLoginFailures(failures);

        if (failures >= maxLoginFailures) {
            user.setStatus(UserStatus.LOCKED);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
        }

        userRepository.save(user);
    }

    private void clearLoginFailures(User user) {
        if (user.getLoginFailures() > 0) {
            user.setLoginFailures(0);
            userRepository.save(user);
        }
    }

    public void logout(String token) {
        try {
            var claims = jwtService.extractClaims(token);
            long ttl = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
            if (ttl > 0) {
                redisTemplate.opsForValue().set("blacklist:" + token, "1", ttl, TimeUnit.SECONDS);
            }
        } catch (Exception ignored) {
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw BusinessException.invalidToken();
        }

        var claims = jwtService.extractRefreshClaims(refreshToken);
        String userId = claims.get("userId", String.class);
        UserRole role = UserRole.valueOf(claims.get("role", String.class));

        User user = userRepository.findById(userId)
                .orElseThrow(BusinessException::invalidToken);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw BusinessException.invalidToken();
        }

        String newAccessToken = jwtService.generateAccessToken(userId, role);
        String newRefreshToken = jwtService.generateRefreshToken(userId, role);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getAccessExpiration() / 1000)
                .role(role)
                .build();
    }

    public void sendResetCode(String phone) {
        if (!userRepository.existsByPhone(phone)) {
            return; // 不暴露手机号是否存在
        }

        String code = String.format("%06d", (int) (Math.random() * 1000000));
        redisTemplate.opsForValue().set("reset:" + phone, code, verificationCodeTtl, TimeUnit.SECONDS);

        // 实际项目中这里发送短信
        System.out.println("Reset code for " + phone + ": " + code);
    }

    public boolean verifyResetCode(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get("reset:" + phone);
        return code.equals(storedCode);
    }

    @Transactional
    public void resetPassword(String phone, String code, String newPassword) {
        if (!verifyResetCode(phone, code)) {
            throw BusinessException.invalidCode();
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            throw BusinessException.weakPassword();
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(BusinessException::invalidCredentials);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 使所有会话失效
        redisTemplate.opsForValue().set("invalidate:" + user.getId(), 
                String.valueOf(System.currentTimeMillis()), 7, TimeUnit.DAYS);

        // 删除验证码
        redisTemplate.delete("reset:" + phone);
    }
}
