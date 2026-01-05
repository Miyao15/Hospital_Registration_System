package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.entity.User;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.PatientRepository;
import com.hospital.registration.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 文件上传Controller
 * 支持头像图片上传功能（存储到数据库）
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${app.upload.max-size:5242880}") // 默认5MB
    private long maxFileSize;

    /**
     * 上传头像图片（存储到数据库）
     * 
     * @param file 图片文件
     * @param authentication 认证信息
     * @return 上传结果，包含Base64数据
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_FILE", "请选择要上传的文件"));
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_FILE_TYPE", "只能上传图片文件"));
            }

            // 验证文件大小
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("FILE_TOO_LARGE", 
                                String.format("文件大小不能超过 %d MB", maxFileSize / 1024 / 1024)));
            }

            // 转换为Base64
            byte[] fileBytes = file.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);
            String dataUrl = "data:" + contentType + ";base64," + base64Data;

            // 获取当前用户（JWT中存储的是userId）
            String userId = authentication.getName();
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
            }

            User user = userOpt.get();

            // 根据用户角色保存到对应表
            String role = user.getRole().name();
            if ("PATIENT".equals(role)) {
                Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
                if (patientOpt.isPresent()) {
                    Patient patient = patientOpt.get();
                    patient.setAvatarData(dataUrl);
                    patient.setAvatarUrl(dataUrl); // 兼容旧字段
                    patientRepository.save(patient);
                }
            } else if ("DOCTOR".equals(role)) {
                Optional<Doctor> doctorOpt = doctorRepository.findByUserId(userId);
                if (doctorOpt.isPresent()) {
                    Doctor doctor = doctorOpt.get();
                    doctor.setAvatarData(dataUrl);
                    doctor.setAvatarUrl(dataUrl); // 兼容旧字段
                    doctorRepository.save(doctor);
                }
            }

            log.info("头像上传成功，用户ID: {}, 大小: {} bytes", userId, fileBytes.length);

            Map<String, String> result = new HashMap<>();
            result.put("url", dataUrl);
            result.put("filename", "avatar_" + userId);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("UPLOAD_FAILED", "文件上传失败: " + e.getMessage()));
        }
    }

    /**
     * 删除头像
     * 
     * @param filename 文件名（兼容旧接口）
     * @param authentication 认证信息
     * @return 删除结果
     */
    @DeleteMapping("/avatar/{filename}")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(
            @PathVariable String filename,
            Authentication authentication) {
        
        try {
            // 获取当前用户（JWT中存储的是userId）
            String userId = authentication.getName();
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
            }

            User user = userOpt.get();
            String role = user.getRole().name();

            // 清除头像数据
            if ("PATIENT".equals(role)) {
                Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
                if (patientOpt.isPresent()) {
                    Patient patient = patientOpt.get();
                    patient.setAvatarData(null);
                    patient.setAvatarUrl(null);
                    patientRepository.save(patient);
                }
            } else if ("DOCTOR".equals(role)) {
                Optional<Doctor> doctorOpt = doctorRepository.findByUserId(userId);
                if (doctorOpt.isPresent()) {
                    Doctor doctor = doctorOpt.get();
                    doctor.setAvatarData(null);
                    doctor.setAvatarUrl(null);
                    doctorRepository.save(doctor);
                }
            }

            log.info("头像删除成功，用户ID: {}", userId);
            return ResponseEntity.ok(ApiResponse.success(null));

        } catch (Exception e) {
            log.error("头像删除失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("DELETE_FAILED", "头像删除失败: " + e.getMessage()));
        }
    }
}

