package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.auth.AuthResponse;
import com.example.OnlineShopping.dto.auth.LoginRequest;
import com.example.OnlineShopping.dto.auth.RegisterRequest;
import com.example.OnlineShopping.entity.User;
import com.example.OnlineShopping.enums.Role;
import com.example.OnlineShopping.enums.UserStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.UserRepository;
import com.example.OnlineShopping.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 验证邮箱或手机号至少提供一个
        if ((request.getEmail() == null || request.getEmail().trim().isEmpty()) &&
            (request.getPhone() == null || request.getPhone().trim().isEmpty())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮箱和手机号至少提供一个");
        }

        // 验证邮箱格式（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS, "邮箱已被注册");
            }
        }

        // 验证手机号格式（如果提供了手机号）
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(request.getPhone()).matches()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "手机号格式不正确");
            }
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException(ErrorCode.PHONE_EXISTS, "手机号已被注册");
            }
        }

        // 创建用户
        User user = User.builder()
                .email(request.getEmail() != null ? request.getEmail().trim() : null)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname().trim() : null)
                .role(Role.CUSTOMER)
                .status(UserStatus.NORMAL)
                .build();

        user = userRepository.save(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());

        // 构建响应
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }

    /**
     * 用户登录
     */
    public AuthResponse login(LoginRequest request) {
        // 查找用户（通过邮箱或手机号）
        User user = userRepository.findByEmailOrPhone(request.getAccount())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_DISABLED, "用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());

        // 构建响应
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }
}

