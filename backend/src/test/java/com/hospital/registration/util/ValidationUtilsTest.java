package com.hospital.registration.util;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证工具类测试
 * 使用 jqwik 进行属性测试
 */
class ValidationUtilsTest {

    // ========== 手机号验证测试 ==========

    @Property(tries = 100)
    void validPhonesShouldPass(@ForAll("validPhones") String phone) {
        assertTrue(ValidationUtils.isValidPhone(phone), 
                "有效手机号应该通过验证: " + phone);
    }

    @Provide
    Arbitrary<String> validPhones() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(9)
                .map(suffix -> "1" + Arbitraries.of("3", "4", "5", "6", "7", "8", "9")
                        .sample() + suffix);
    }

    @Test
    void invalidPhonesShouldFail() {
        assertFalse(ValidationUtils.isValidPhone(null));
        assertFalse(ValidationUtils.isValidPhone(""));
        assertFalse(ValidationUtils.isValidPhone("123"));
        assertFalse(ValidationUtils.isValidPhone("0123456789"));  // 不以1开头
        assertFalse(ValidationUtils.isValidPhone("12345678901")); // 第二位是2
        assertFalse(ValidationUtils.isValidPhone("1234567890123")); // 太长
    }

    // ========== 身份证验证测试 ==========

    @Property(tries = 100)
    void validIdCardsShouldPass(@ForAll("validIdCards") String idCard) {
        assertTrue(ValidationUtils.isValidIdCard(idCard), 
                "有效身份证应该通过验证: " + idCard);
    }

    @Provide
    Arbitrary<String> validIdCards() {
        // 生成有效的身份证号（包含正确的校验码）
        String[] regions = {"110101", "110102", "310101", "440101", "510101"};
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        return Arbitraries.of(regions).flatMap(region ->
            Arbitraries.integers().between(1950, 2005).flatMap(year ->
                Arbitraries.integers().between(1, 12).flatMap(month ->
                    Arbitraries.integers().between(1, 28).flatMap(day ->
                        Arbitraries.integers().between(1, 999).map(seq -> {
                            String base = String.format("%s%04d%02d%02d%03d", 
                                    region, year, month, day, seq);
                            int sum = 0;
                            for (int i = 0; i < 17; i++) {
                                sum += (base.charAt(i) - '0') * weights[i];
                            }
                            return base + checkCodes[sum % 11];
                        })
                    )
                )
            )
        );
    }

    @Test
    void invalidIdCardsShouldFail() {
        assertFalse(ValidationUtils.isValidIdCard(null));
        assertFalse(ValidationUtils.isValidIdCard(""));
        assertFalse(ValidationUtils.isValidIdCard("12345678901234567")); // 17位
        assertFalse(ValidationUtils.isValidIdCard("12345678901234567X")); // 校验码错误
    }

    // ========== 密码强度验证测试 ==========

    @Property(tries = 100)
    void validPasswordsShouldPass(@ForAll("validPasswords") String password) {
        assertTrue(ValidationUtils.isValidPassword(password), 
                "有效密码应该通过验证: " + password);
    }

    @Provide
    Arbitrary<String> validPasswords() {
        // 生成至少8位，包含字母和数字的密码
        // 确保字母部分至少5位，数字部分至少3位，总长度至少8位
        Arbitrary<String> letters = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5).ofMaxLength(10);
        Arbitrary<String> digits = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(3).ofMaxLength(6);
        
        return Combinators.combine(letters, digits)
                .as((l, d) -> l + d);
    }

    @Test
    void invalidPasswordsShouldFail() {
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword("abc123")); // 太短
        assertFalse(ValidationUtils.isValidPassword("abcdefgh")); // 没有数字
        assertFalse(ValidationUtils.isValidPassword("12345678")); // 没有字母
    }

    // ========== 医师资格证验证测试 ==========

    @Property(tries = 100)
    void validLicenseNumbersShouldPass(@ForAll("validLicenseNumbers") String license) {
        assertTrue(ValidationUtils.isValidLicenseNumber(license), 
                "有效资格证号应该通过验证: " + license);
    }

    @Provide
    Arbitrary<String> validLicenseNumbers() {
        return Arbitraries.strings()
                .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
                .ofMinLength(10).ofMaxLength(20);
    }

    @Test
    void invalidLicenseNumbersShouldFail() {
        assertFalse(ValidationUtils.isValidLicenseNumber(null));
        assertFalse(ValidationUtils.isValidLicenseNumber(""));
        assertFalse(ValidationUtils.isValidLicenseNumber("ABC123")); // 太短
        assertFalse(ValidationUtils.isValidLicenseNumber("ABC123!@#")); // 包含特殊字符
    }
}
