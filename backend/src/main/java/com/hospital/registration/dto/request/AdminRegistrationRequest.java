package com.hospital.registration.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminRegistrationRequest {

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能少于8位")
    private String password;

    @NotEmpty(message = "姓名不能为空")
    private String name;

    @NotEmpty(message = "工号不能为空")
    private String employeeId;

    @NotEmpty(message = "管理员注册密钥不能为空")
    private String adminRegistrationKey;
}
