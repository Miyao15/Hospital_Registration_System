package com.hospital.registration.dto.response;

import com.hospital.registration.enums.UserRole;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserRole role;
}
