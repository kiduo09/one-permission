package com.zhangyu.permission.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 * 用于生成BCrypt加密密码
 * 
 * @author zhangyu
 */
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 生成BCrypt加密密码
     * 
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 测试方法：生成密码哈希
     * 运行此方法可以生成新的密码哈希值
     */
    public static void main(String[] args) {
        // 生成123456的BCrypt密码
        String password = "123456";
        String encoded = encode(password);
        System.out.println("明文密码: " + password);
        System.out.println("BCrypt密码: " + encoded);
        System.out.println("验证结果: " + matches(password, encoded));
        
        // 生成admin的BCrypt密码
        String adminPassword = "admin";
        String adminEncoded = encode(adminPassword);
        System.out.println("\n明文密码: " + adminPassword);
        System.out.println("BCrypt密码: " + adminEncoded);
        System.out.println("验证结果: " + matches(adminPassword, adminEncoded));
    }
}

