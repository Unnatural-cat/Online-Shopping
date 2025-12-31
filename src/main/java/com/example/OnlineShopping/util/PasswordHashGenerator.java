package com.example.OnlineShopping.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具（用于生成初始化数据的密码哈希）
 * 注意：此工具类仅用于开发和测试环境
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希值: " + hash);
        System.out.println("\n验证哈希值是否正确:");
        System.out.println("验证结果: " + encoder.matches(password, hash));
    }
}

