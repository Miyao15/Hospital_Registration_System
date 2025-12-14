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
     * 验证身份证号格式（包含校验码验证）
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) return false;
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) return false;
        
        // 校验码验证
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weights[i];
        }
        
        char expectedCheckCode = checkCodes[sum % 11];
        return Character.toUpperCase(idCard.charAt(17)) == expectedCheckCode;
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
