package com.hospital.registration.dto.request;

import com.hospital.registration.enums.DoctorTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorRegistrationRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式无效")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "工号不能为空")
    private String employeeId;

    @NotNull(message = "职称不能为空")
    private DoctorTitle title;

    @NotBlank(message = "科室不能为空")
    private String departmentId;

    private String specialty;

    @NotBlank(message = "医师资格证号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{10,20}$", message = "资格证格式无效")
    private String licenseNumber;

    private String introduction;
}
