package com.hospital.registration.security;

import com.hospital.registration.enums.UserRole;
import io.jsonwebtoken.Claims;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 属性测试：JWT 验证
 * 
 * **Feature: user-authentication, Property 9: 受保护请求的 JWT 验证**
 * **Validates: Requirements 3.4, 3.5**
 */
class JwtValidationPropertyTest {

    private JwtService jwtService;

    // 测试用密钥（至少32字节）
    private static final String TEST_SECRET = "test-secret-key-for-jwt-signing-minimum-32-bytes";
    private static final String TEST_REFRESH_SECRET = "test-refresh-secret-key-for-jwt-minimum-32-bytes";
    private static final long ACCESS_EXPIRATION = 7200000L; // 2小时
    private static final long REFRESH_EXPIRATION = 604800000L; // 7天

    @BeforeProperty
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "secret", TEST_SECRET);
        setField(jwtService, "refreshSecret", TEST_REFRESH_SECRET);
        setField(jwtService, "accessExpiration", ACCESS_EXPIRATION);
        setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ========== 属性 9: 受保护请求的 JWT 验证 ==========

    /**
     * **Feature: user-authentication, Property 9: 受保护请求的 JWT 验证**
     * **Validates: Requirements 3.4, 3.5**
     * 
     * 对于任意有效的用户ID和角色，生成的JWT令牌应该是有效的，
     * 并且能够正确提取用户ID和角色信息。
     */
    @Property(tries = 100)
    void validTokenShouldBeAccepted(
            @ForAll("validUserIds") String userId,
            @ForAll("validRoles") UserRole role) {

        // 生成令牌
        String token = jwtService.generateAccessToken(userId, role);

        // 验证令牌有效
        assertTrue(jwtService.isTokenValid(token), "有效令牌应该通过验证");

        // 验证能正确提取用户ID
        assertEquals(userId, jwtService.extractUserId(token), "应该能正确提取用户ID");

        // 验证能正确提取角色
        assertEquals(role, jwtService.extractRole(token), "应该能正确提取角色");
    }

    /**
     * **Feature: user-authentication, Property 9: 受保护请求的 JWT 验证**
     * **Validates: Requirements 3.4, 3.5**
     * 
     * 对于任意无效的令牌字符串，验证应该失败。
     */
    @Property(tries = 100)
    void invalidTokenShouldBeRejected(@ForAll("invalidTokens") String invalidToken) {
        assertFalse(jwtService.isTokenValid(invalidToken), "无效令牌应该被拒绝: " + invalidToken);
    }

    /**
     * **Feature: user-authentication, Property 9: 受保护请求的 JWT 验证**
     * **Validates: Requirements 3.4, 3.5**
     * 
     * 令牌中的claims应该包含正确的用户信息。
     */
    @Property(tries = 100)
    void tokenClaimsShouldContainCorrectUserInfo(
            @ForAll("validUserIds") String userId,
            @ForAll("validRoles") UserRole role) {

        String token = jwtService.generateAccessToken(userId, role);
        Claims claims = jwtService.extractClaims(token);

        assertEquals(userId, claims.get("userId", String.class));
        assertEquals(role.name(), claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    /**
     * **Feature: user-authentication, Property 9: 受保护请求的 JWT 验证**
     * **Validates: Requirements 3.4, 3.5**
     * 
     * 刷新令牌应该使用不同的密钥，且有效期更长。
     */
    @Property(tries = 100)
    void refreshTokenShouldBeValidAndHaveLongerExpiration(
            @ForAll("validUserIds") String userId,
            @ForAll("validRoles") UserRole role) {

        String accessToken = jwtService.generateAccessToken(userId, role);
        String refreshToken = jwtService.generateRefreshToken(userId, role);

        // 两个令牌应该不同
        assertNotEquals(accessToken, refreshToken);

        // 刷新令牌应该有效
        assertTrue(jwtService.isRefreshTokenValid(refreshToken));

        // 刷新令牌不应该用访问令牌的密钥验证
        assertFalse(jwtService.isTokenValid(refreshToken));

        // 访问令牌不应该用刷新令牌的密钥验证
        assertFalse(jwtService.isRefreshTokenValid(accessToken));
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<String> validUserIds() {
        return Arbitraries.strings()
                .withCharRange('a', 'f')
                .withCharRange('0', '9')
                .ofLength(36)
                .map(s -> s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + 
                         s.substring(12, 16) + "-" + s.substring(16, 20) + "-" + s.substring(20, 32));
    }

    @Provide
    Arbitrary<UserRole> validRoles() {
        return Arbitraries.of(UserRole.values());
    }

    @Provide
    Arbitrary<String> invalidTokens() {
        return Arbitraries.oneOf(
                // 空字符串
                Arbitraries.just(""),
                // 随机字符串
                Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(50),
                // 格式错误的JWT（缺少部分）
                Arbitraries.strings().alpha().ofLength(20).map(s -> s + "."),
                // 格式错误的JWT（两部分）
                Arbitraries.strings().alpha().ofLength(20).map(s -> s + "." + s),
                // 篡改的令牌
                Arbitraries.just("eyJhbGciOiJIUzI1NiJ9.eyJ0ZXN0IjoidGVzdCJ9.invalid_signature")
        );
    }
}
