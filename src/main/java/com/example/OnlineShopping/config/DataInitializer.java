package com.example.OnlineShopping.config;

import com.example.OnlineShopping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据初始化器
 * 在应用启动时检查并修复用户密码哈希值
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 测试账号配置
    private static final String TEST_PASSWORD = "password123";
    private static final List<String> TEST_EMAILS = Arrays.asList(
        "admin@example.com",
        "customer1@example.com",
        "customer2@example.com"
    );
    
    @Override
    @Transactional
    public void run(String... args) {
        log.info("开始检查并修复测试用户密码哈希值...");
        
        AtomicInteger fixedCount = new AtomicInteger(0);
        for (String email : TEST_EMAILS) {
            userRepository.findByEmail(email).ifPresent(user -> {
                // 检查密码哈希值是否正确
                // 如果密码哈希值无法验证，则重新生成
                try {
                    if (!passwordEncoder.matches(TEST_PASSWORD, user.getPasswordHash())) {
                        log.warn("检测到用户 {} 的密码哈希值不正确，正在修复...", email);
                        String newHash = passwordEncoder.encode(TEST_PASSWORD);
                        user.setPasswordHash(newHash);
                        userRepository.save(user);
                        fixedCount.incrementAndGet();
                        log.info("已修复用户 {} 的密码哈希值", email);
                    } else {
                        log.debug("用户 {} 的密码哈希值正确", email);
                    }
                } catch (Exception e) {
                    // 如果验证过程出错（可能是哈希值格式不正确），直接修复
                    log.warn("用户 {} 的密码哈希值验证失败，正在修复...", email, e);
                    String newHash = passwordEncoder.encode(TEST_PASSWORD);
                    user.setPasswordHash(newHash);
                    userRepository.save(user);
                    fixedCount.incrementAndGet();
                    log.info("已修复用户 {} 的密码哈希值", email);
                }
            });
        }
        
        int count = fixedCount.get();
        if (count > 0) {
            log.info("用户密码哈希值检查完成，修复了 {} 个用户", count);
        } else {
            log.info("用户密码哈希值检查完成，所有用户密码哈希值正确");
        }
    }
}

