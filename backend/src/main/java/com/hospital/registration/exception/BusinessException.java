package com.hospital.registration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final HttpStatus httpStatus;
    private final Object details;

    public BusinessException(String message) {
        super(message);
        this.code = "BIZ_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public BusinessException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    public BusinessException(String code, String message, HttpStatus httpStatus, Object details) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    // 认证错误
    public static BusinessException invalidCredentials() {
        return new BusinessException("AUTH_001", "凭证无效", HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException tokenExpired() {
        return new BusinessException("AUTH_002", "令牌已过期", HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException invalidToken() {
        return new BusinessException("AUTH_003", "令牌无效", HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException accountLocked() {
        return new BusinessException("AUTH_004", "账户已锁定", HttpStatus.FORBIDDEN);
    }

    public static BusinessException accountPending() {
        return new BusinessException("AUTH_005", "账户待审批", HttpStatus.FORBIDDEN);
    }

    public static BusinessException accountDisabled() {
        return new BusinessException("AUTH_006", "账户已禁用", HttpStatus.FORBIDDEN);
    }

    public static BusinessException accessDenied() {
        return new BusinessException("AUTH_007", "权限不足", HttpStatus.FORBIDDEN);
    }

    public static BusinessException invalidAdminKey() {
        return new BusinessException("AUTH_008", "无效的管理员注册密钥", HttpStatus.FORBIDDEN);
    }

    // 注册错误
    public static BusinessException invalidPhone() {
        return new BusinessException("REG_001", "手机号格式无效", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException invalidIdCard() {
        return new BusinessException("REG_002", "身份证格式无效", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException invalidLicense() {
        return new BusinessException("REG_003", "资格证格式无效", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException phoneExists() {
        return new BusinessException("REG_004", "手机号已注册", HttpStatus.CONFLICT);
    }

    public static BusinessException idCardExists() {
        return new BusinessException("REG_005", "身份证已注册", HttpStatus.CONFLICT);
    }

    public static BusinessException employeeIdExists() {
        return new BusinessException("REG_006", "工号已注册", HttpStatus.CONFLICT);
    }
    
    public static BusinessException licenseNumberExists() {
        return new BusinessException("REG_007", "医师资格证号已注册", HttpStatus.CONFLICT);
    }
    
    public static BusinessException departmentNotFound() {
        return new BusinessException("REG_008", "科室不存在", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException missingFields(String fields) {
        return new BusinessException("REG_007", "缺少必填字段", HttpStatus.BAD_REQUEST, fields);
    }

    // 密码错误
    public static BusinessException weakPassword() {
        return new BusinessException("PWD_001", "密码强度不足", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException wrongPassword() {
        return new BusinessException("PWD_002", "当前密码错误", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException invalidCode() {
        return new BusinessException("PWD_003", "验证码无效", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException codeExpired() {
        return new BusinessException("PWD_004", "验证码已过期", HttpStatus.BAD_REQUEST);
    }

    // 通用错误
    public static BusinessException notFound(String resource) {
        return new BusinessException("NOT_FOUND", resource + "不存在", HttpStatus.NOT_FOUND);
    }
}
