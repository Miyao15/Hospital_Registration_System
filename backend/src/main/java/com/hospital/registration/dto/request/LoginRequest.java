package com.hospital.registration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String identifier; // 手机号或工号

    @NotBlank(message = "密码不能为空")
    private String password;
}
