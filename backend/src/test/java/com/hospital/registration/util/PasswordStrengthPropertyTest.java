package com.hospital.registration.util;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 属性测试：密码强度验证
 * 
 * **Feature: user-authentication, Property 14: 密码强度验证**
 * **Validates: Requirements 4.5**
 * 
 * 对于任意不符合强度要求的密码（少于8个字符、缺少字母或缺少数字），
 * 系统应拒绝密码修改。
 */
class PasswordStrengthPropertyTest {

    // ========== 属性 14: 密码强度验证 ==========

    /**
     * 属性测试：有效密码应该通过验证
     * 有效密码：至少8个字符，包含字母和数字
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void validPasswordsShouldPass(@ForAll("validPasswords") String password) {
        assertTrue(ValidationUtils.isValidPassword(password),
                "有效密码应该通过验证: " + password + " (长度: " + password.length() + ")");
    }

    @Provide
    Arbitrary<String> validPasswords() {
        // 生成至少8位，包含字母和数字的密码
        Arbitrary<String> letters = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5).ofMaxLength(10);
        Arbitrary<String> digits = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(3).ofMaxLength(6);
        
        return Combinators.combine(letters, digits)
                .as((l, d) -> l + d);
    }

    /**
     * 属性测试：太短的密码应该被拒绝
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void tooShortPasswordsShouldBeRejected(@ForAll("tooShortPasswords") String password) {
        assertFalse(ValidationUtils.isValidPassword(password),
                "太短的密码应该被拒绝: " + password + " (长度: " + password.length() + ")");
    }

    @Provide
    Arbitrary<String> tooShortPasswords() {
        // 生成少于8个字符的密码（即使包含字母和数字）
        return Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz0123456789")
                .ofMinLength(1).ofMaxLength(7)
                .filter(s -> s.matches(".*[a-zA-Z].*") && s.matches(".*\\d.*"));
    }

    /**
     * 属性测试：只有字母的密码应该被拒绝
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void lettersOnlyPasswordsShouldBeRejected(@ForAll("lettersOnlyPasswords") String password) {
        assertFalse(ValidationUtils.isValidPassword(password),
                "只有字母的密码应该被拒绝: " + password);
    }

    @Provide
    Arbitrary<String> lettersOnlyPasswords() {
        // 生成只有字母的密码（至少8个字符）
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(8).ofMaxLength(16);
    }

    /**
     * 属性测试：只有数字的密码应该被拒绝
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void digitsOnlyPasswordsShouldBeRejected(@ForAll("digitsOnlyPasswords") String password) {
        assertFalse(ValidationUtils.isValidPassword(password),
                "只有数字的密码应该被拒绝: " + password);
    }

    @Provide
    Arbitrary<String> digitsOnlyPasswords() {
        // 生成只有数字的密码（至少8个字符）
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(8).ofMaxLength(16);
    }

    /**
     * 属性测试：空密码应该被拒绝
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 10)
    void emptyOrNullPasswordsShouldBeRejected(@ForAll("emptyPasswords") String password) {
        assertFalse(ValidationUtils.isValidPassword(password),
                "空密码应该被拒绝");
    }

    @Provide
    Arbitrary<String> emptyPasswords() {
        return Arbitraries.of("", " ", "  ", "\t", "\n");
    }

    /**
     * 属性测试：包含大写字母的有效密码应该通过
     * 
     * **Feature: user-authentication, Property 14: 密码强度验证**
     * **Validates: Requirements 4.5**
     */
    @Property(tries = 100)
    void validPasswordsWithUppercaseShouldPass(@ForAll("validPasswordsWithUppercase") String password) {
        assertTrue(ValidationUtils.isValidPassword(password),
                "包含大写字母的有效密码应该通过: " + password);
    }

    @Provide
    Arbitrary<String> validPasswordsWithUppercase() {
        // 确保总长度至少8位：3+3+4=10位最小
        Arbitrary<String> lowerLetters = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3).ofMaxLength(5);
        Arbitrary<String> upperLetters = Arbitraries.strings()
                .withCharRange('A', 'Z')
                .ofMinLength(3).ofMaxLength(5);
        Arbitrary<String> digits = Arbitraries.strings()
                .withCharRange('0', '9')
                .ofMinLength(4).ofMaxLength(6);
        
        return Combinators.combine(lowerLetters, upperLetters, digits)
                .as((l, u, d) -> l + u + d);
    }
}
