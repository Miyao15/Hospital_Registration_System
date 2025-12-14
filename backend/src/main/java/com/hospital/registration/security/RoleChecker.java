package com.hospital.registration.security;

import com.hospital.registration.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RBAC 角色检查工具
 */
@Component
public class RoleChecker {

    /**
     * 检查用户角色是否在允许的角色列表中
     */
    public boolean hasRole(UserRole userRole, UserRole... allowedRoles) {
        if (userRole == null || allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }
        Set<UserRole> allowed = Arrays.stream(allowedRoles).collect(Collectors.toSet());
        return allowed.contains(userRole);
    }

    /**
     * 检查是否是管理员
     */
    public boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }

    /**
     * 检查是否是医师
     */
    public boolean isDoctor(UserRole role) {
        return role == UserRole.DOCTOR;
    }

    /**
     * 检查是否是患者
     */
    public boolean isPatient(UserRole role) {
        return role == UserRole.PATIENT;
    }

    /**
     * 检查是否是医疗人员（医师或管理员）
     */
    public boolean isMedicalStaff(UserRole role) {
        return role == UserRole.DOCTOR || role == UserRole.ADMIN;
    }
}
