package com.hospital.registration.dto.response;

import com.hospital.registration.enums.UserRole;
import com.hospital.registration.enums.UserStatus;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private Object profile; // Patient, Doctor, or Admin details
}
