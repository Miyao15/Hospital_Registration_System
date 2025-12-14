package com.hospital.registration.controller;

import com.hospital.registration.dto.request.DoctorRegistrationRequest;
import com.hospital.registration.dto.request.LoginRequest;
import com.hospital.registration.dto.request.PatientRegistrationRequest;
import com.hospital.registration.dto.request.ResetPasswordRequest;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.response.LoginResponse;
import com.hospital.registration.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
