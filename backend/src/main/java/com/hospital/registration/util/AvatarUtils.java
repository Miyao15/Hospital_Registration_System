package com.hospital.registration.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 头像工具类
 * 提供默认头像生成和头像URL处理功能
 */
public class AvatarUtils {
    
    // 默认头像URL（Element Plus默认头像）
    private static final String DEFAULT_AVATAR = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
    
    // UI Avatars API - 基于姓名生成头像
    private static final String UI_AVATARS_BASE_URL = "https://ui-avatars.com/api/";
    
    /**
     * 获取默认头像URL
     */
    public static String getDefaultAvatar() {
        return DEFAULT_AVATAR;
    }
    
    /**
     * 根据姓名生成头像URL（使用UI Avatars API）
     * 
     * @param name 姓名
     * @return 头像URL
     */
    public static String generateAvatarFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT_AVATAR;
        }
        
        try {
            // 获取姓名的第一个字符（支持中文）
            String firstChar = name.trim().substring(0, 1);
            // URL编码
            String encodedName = URLEncoder.encode(firstChar, StandardCharsets.UTF_8);
            
            // 生成头像URL：圆形、128x128、背景色随机、文字颜色白色
            return String.format("%s?name=%s&size=128&background=random&color=fff&bold=true&format=png",
                    UI_AVATARS_BASE_URL, encodedName);
        } catch (Exception e) {
            return DEFAULT_AVATAR;
        }
    }
    
    /**
     * 根据姓名和性别生成头像URL
     * 
     * @param name 姓名
     * @param gender 性别（MALE/FEMALE），用于选择不同的背景色
     * @return 头像URL
     */
    public static String generateAvatarFromNameAndGender(String name, String gender) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT_AVATAR;
        }
        
        try {
            String firstChar = name.trim().substring(0, 1);
            String encodedName = URLEncoder.encode(firstChar, StandardCharsets.UTF_8);
            
            // 根据性别选择不同的背景色范围
            String background = "random"; // 可以改为固定颜色，如：MALE -> "4A90E2", FEMALE -> "E94B8B"
            
            return String.format("%s?name=%s&size=128&background=%s&color=fff&bold=true&format=png",
                    UI_AVATARS_BASE_URL, encodedName, background);
        } catch (Exception e) {
            return DEFAULT_AVATAR;
        }
    }
    
    /**
     * 获取头像URL，如果为空则生成默认头像
     * 
     * @param avatarUrl 现有的头像URL（可能为null）
     * @param name 姓名（用于生成默认头像）
     * @return 头像URL
     */
    public static String getAvatarUrl(String avatarUrl, String name) {
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            return avatarUrl;
        }
        return generateAvatarFromName(name);
    }
    
    /**
     * 验证头像URL是否有效（简单检查）
     * 
     * @param avatarUrl 头像URL
     * @return 是否为有效的URL格式
     */
    public static boolean isValidAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return false;
        }
        // 简单检查：以http://或https://开头
        String trimmed = avatarUrl.trim().toLowerCase();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }
}

