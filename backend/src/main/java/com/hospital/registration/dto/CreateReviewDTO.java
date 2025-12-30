package com.hospital.registration.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewDTO {
    @NotBlank(message = "医生ID不能为空")
    private String doctorId;
    
    private String appointmentId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer rating;
    
    @NotBlank(message = "评价内容不能为空")
    private String content;
}

