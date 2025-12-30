package com.hospital.registration.controller;

import com.hospital.registration.dto.request.DoctorRegistrationRequest;
import com.hospital.registration.dto.request.AdminRegistrationRequest;
import com.hospital.registration.dto.request.LoginRequest;
import com.hospital.registration.dto.request.PatientRegistrationRequest;
import com.hospital.registration.dto.request.ResetPasswordRequest;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.response.LoginResponse;
import com.hospital.registration.entity.User;
import com.hospital.registration.repository.UserRepository;
import com.hospital.registration.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register/patient")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        String userId = authService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("userId", userId, "message", "患者注册成功")));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request) {
        String userId = authService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("userId", userId, "message", "医师注册成功，请等待管理员审批")));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerAdmin(
            @Valid @RequestBody AdminRegistrationRequest request) {
        String userId = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("userId", userId, "message", "管理员注册成功")));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "登出成功")));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendResetCode(@RequestBody Map<String, String> request) {
        authService.sendResetCode(request.get("phone"));
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "验证码已发送")));
    }

    @PostMapping("/reset-password/verify")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyResetCode(@RequestBody Map<String, String> request) {
        boolean valid = authService.verifyResetCode(request.get("phone"), request.get("code"));
        return ResponseEntity.ok(ApiResponse.success(Map.of("valid", valid)));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getPhone(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "密码重置成功")));
    }
    
    /**
     * 调试端点 - 检查用户状态和密码
     * 正式环境请删除此端点
     */
    @GetMapping("/debug/check-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugCheckUser(
            @RequestParam String phone,
            @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userRepository.findByPhone(phone).orElse(null);
        
        if (user == null) {
            result.put("userFound", false);
            result.put("message", "用户不存在，请检查手机号是否正确");
        } else {
            result.put("userFound", true);
            result.put("userId", user.getId());
            result.put("role", user.getRole().name());
            result.put("status", user.getStatus().name());
            result.put("passwordHashPrefix", user.getPasswordHash().substring(0, 20) + "...");
            
            // 验证密码
            boolean passwordMatch = passwordEncoder.matches(password, user.getPasswordHash());
            result.put("passwordMatch", passwordMatch);
            
            if (!passwordMatch) {
                // 生成正确的哈希供参考
                String correctHash = passwordEncoder.encode(password);
                result.put("newHashExample", correctHash);
                result.put("message", "密码不匹配！可能是数据库中的哈希值格式不正确");
            } else {
                result.put("message", "密码正确，检查账户状态");
            }
        }
        
        log.info("调试检查用户: phone={}, result={}", phone, result);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * 调试端点 - 重置用户密码为简单密码
     * 正式环境请删除此端点
     */
    @PostMapping("/debug/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> debugResetPassword(
            @RequestParam String phone,
            @RequestParam String newPassword) {
        User user = userRepository.findByPhone(phone).orElse(null);
        
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.success(Map.of("message", "用户不存在")));
        }
        
        String newHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newHash);
        userRepository.save(user);
        
        log.info("已重置用户密码: phone={}", phone);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "message", "密码已重置",
            "phone", phone,
            "newPassword", newPassword
        )));
    }
}
