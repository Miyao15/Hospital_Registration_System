package com.hospital.registration.controller;

import com.hospital.registration.dto.request.ChangePasswordRequest;
import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.dto.response.UserProfileResponse;
import com.hospital.registration.enums.UserRole;
import com.hospital.registration.security.JwtService;
import com.hospital.registration.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, Object> updateData) {
        String userId = (String) authentication.getPrincipal();
        // 从认证信息中获取角色
        String roleStr = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        UserRole role = UserRole.valueOf(roleStr);
        
        UserProfileResponse profile = userService.updateProfile(userId, role, updateData);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Map<String, String>>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        String userId = (String) authentication.getPrincipal();
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "密码修改成功")));
    }
}
