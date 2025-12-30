package com.hospital.registration.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DoctorLeaveDTO {
    private String id;
    private String doctorId;
    private String doctorName;
    private String departmentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer days;  // 请假天数
    private String reason;
    private String status;
    private String statusName;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectReason;
    private LocalDateTime createdAt;
    private String reviewNote;  // 审批意见（拒绝原因或备注）
}
