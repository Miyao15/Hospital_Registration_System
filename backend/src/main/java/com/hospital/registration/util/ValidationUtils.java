package com.hospital.registration.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtils {
    
    // 中国手机号正则：1[3-9]开头的11位数字
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 身份证号正则：17位数字+1位校验码(数字或X)
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^\\d{17}[\\dX]$");
    
    // 医师资格证正则：10-20位字母数字
    private static final Pattern LICENSE_PATTERN = Pattern.compile("^[A-Za-z0-9]{10,20}$");
    
    // 密码正则：至少8位，包含字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证身份证号格式（简化验证，只检查格式）
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) return false;
        // 只验证格式：17位数字+1位校验码(数字或X)
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 验证医师资格证格式
     */
    public static boolean isValidLicenseNumber(String license) {
        if (license == null || license.isEmpty()) return false;
        return LICENSE_PATTERN.matcher(license).matches();
    }

    /**
     * 验证密码强度
     * 要求：至少8个字符，包含字母和数字
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
