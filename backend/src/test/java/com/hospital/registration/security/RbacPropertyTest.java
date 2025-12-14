package com.hospital.registration.security;

import com.hospital.registration.enums.UserRole;
import net.jqwik.api.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 属性测试：RBAC 权限验证
 * 
 * **Feature: user-authentication, Property 15: RBAC 权限验证**
 * **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
 */
class RbacPropertyTest {

    private final RoleChecker roleChecker = new RoleChecker();

    // ========== 属性 15: RBAC 权限验证 ==========

    /**
     * **Feature: user-authentication, Property 15: RBAC 权限验证**
     * **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
     * 
     * 对于任意用户角色和允许的角色列表，当用户角色在允许列表中时应返回true
     */
    @Property(tries = 100)
    void userWithAllowedRoleShouldHaveAccess(
            @ForAll("validRoles") UserRole userRole,
            @ForAll("roleArraysContaining") RoleArrayWithTarget rolesWithTarget) {

        // 确保用户角色在允许列表中
        Assume.that(rolesWithTarget.contains(userRole));

        assertTrue(roleChecker.hasRole(userRole, rolesWithTarget.roles()),
                "用户角色 " + userRole + " 应该在允许列表中有访问权限");
    }

    /**
     * **Feature: user-authentication, Property 15: RBAC 权限验证**
     * **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
     * 
     * 对于任意用户角色和不包含该角色的允许列表，应返回false
     */
    @Property(tries = 100)
    void userWithoutAllowedRoleShouldBeDenied(
            @ForAll("validRoles") UserRole userRole,
            @ForAll("roleArraysNotContaining") RoleArrayWithTarget rolesWithTarget) {

        // 确保用户角色不在允许列表中
        Assume.that(!rolesWithTarget.contains(userRole));

        assertFalse(roleChecker.hasRole(userRole, rolesWithTarget.roles()),
                "用户角色 " + userRole + " 不应该有访问权限");
    }

    /**
     * 管理员角色检查
     */
    @Property(tries = 100)
    void adminCheckShouldBeCorrect(@ForAll("validRoles") UserRole role) {
        boolean expected = role == UserRole.ADMIN;
        assertEquals(expected, roleChecker.isAdmin(role));
    }

    /**
     * 医师角色检查
     */
    @Property(tries = 100)
    void doctorCheckShouldBeCorrect(@ForAll("validRoles") UserRole role) {
        boolean expected = role == UserRole.DOCTOR;
        assertEquals(expected, roleChecker.isDoctor(role));
    }

    /**
     * 患者角色检查
     */
    @Property(tries = 100)
    void patientCheckShouldBeCorrect(@ForAll("validRoles") UserRole role) {
        boolean expected = role == UserRole.PATIENT;
        assertEquals(expected, roleChecker.isPatient(role));
    }

    /**
     * 医疗人员检查（医师或管理员）
     */
    @Property(tries = 100)
    void medicalStaffCheckShouldBeCorrect(@ForAll("validRoles") UserRole role) {
        boolean expected = role == UserRole.DOCTOR || role == UserRole.ADMIN;
        assertEquals(expected, roleChecker.isMedicalStaff(role));
    }

    /**
     * 空角色应该被拒绝
     */
    @Property(tries = 50)
    void nullRoleShouldBeDenied(@ForAll("nonEmptyRoleArrays") UserRole[] allowedRoles) {
        assertFalse(roleChecker.hasRole(null, allowedRoles));
    }

    /**
     * 空允许列表应该拒绝所有角色
     */
    @Property(tries = 50)
    void emptyAllowedRolesShouldDenyAll(@ForAll("validRoles") UserRole userRole) {
        assertFalse(roleChecker.hasRole(userRole));
        assertFalse(roleChecker.hasRole(userRole, (UserRole[]) null));
    }

    // ========== 生成器 ==========

    @Provide
    Arbitrary<UserRole> validRoles() {
        return Arbitraries.of(UserRole.values());
    }

    @Provide
    Arbitrary<UserRole[]> nonEmptyRoleArrays() {
        return Arbitraries.of(UserRole.values())
                .list().ofMinSize(1).ofMaxSize(3)
                .map(list -> list.toArray(new UserRole[0]));
    }

    @Provide
    Arbitrary<RoleArrayWithTarget> roleArraysContaining() {
        return Arbitraries.of(UserRole.values())
                .list().ofMinSize(1).ofMaxSize(3)
                .map(list -> new RoleArrayWithTarget(list.toArray(new UserRole[0])));
    }

    @Provide
    Arbitrary<RoleArrayWithTarget> roleArraysNotContaining() {
        return Arbitraries.of(UserRole.values())
                .list().ofMinSize(1).ofMaxSize(2)
                .map(list -> new RoleArrayWithTarget(list.toArray(new UserRole[0])));
    }

    // 辅助类
    record RoleArrayWithTarget(UserRole[] roles) {
        boolean contains(UserRole role) {
            return Arrays.asList(roles).contains(role);
        }
    }
}
