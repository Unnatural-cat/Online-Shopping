package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.auth.AuthResponse;
import com.example.OnlineShopping.dto.auth.LoginRequest;
import com.example.OnlineShopping.dto.auth.RegisterRequest;
import com.example.OnlineShopping.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseResult<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseResult.success("注册成功", response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseResult<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseResult.success("登录成功", response);
    }
}

