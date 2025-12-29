package com.hospital.registration.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.registration.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式无效")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dX]$", message = "身份证格式无效")
    private String idCard;

    @NotNull(message = "性别不能为空")
    private Gender gender;

    @NotNull(message = "出生日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String medicalHistory;
    private String allergyHistory;
    private String emergencyContact;
    private String emergencyPhone;
}
