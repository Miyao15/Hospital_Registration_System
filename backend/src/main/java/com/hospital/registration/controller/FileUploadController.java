package com.hospital.registration.controller;

import com.hospital.registration.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Controller
 * 支持头像图片上传功能
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Value("${app.upload.max-size:5242880}") // 默认5MB
    private long maxFileSize;

    /**
     * 上传头像图片
     * 
     * @param file 图片文件
     * @param authentication 认证信息
     * @return 上传结果，包含文件URL
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

            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 生成访问URL
            String fileUrl = urlPrefix + "/avatars/" + filename;

            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("UPLOAD_FAILED", "文件上传失败: " + e.getMessage()));
        }
    }

    /**
     * 删除头像文件
     * 
     * @param filename 文件名
     * @param authentication 认证信息
     * @return 删除结果
     */
    @DeleteMapping("/avatar/{filename}")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(
            @PathVariable String filename,
            Authentication authentication) {
        
        try {
            Path filePath = Paths.get(uploadDir, "avatars", filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("FILE_NOT_FOUND", "文件不存在"));
            }

            Files.delete(filePath);
            log.info("文件删除成功: {}", filename);

            return ResponseEntity.ok(ApiResponse.success(null));

        } catch (IOException e) {
            log.error("文件删除失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("DELETE_FAILED", "文件删除失败: " + e.getMessage()));
        }
    }
}

