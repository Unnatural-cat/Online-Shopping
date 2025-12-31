package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.user.UserDetailResponse;
import com.example.OnlineShopping.dto.user.UserListResponse;
import com.example.OnlineShopping.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器（管理端）
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    /**
     * 用户列表
     */
    @GetMapping
    public ResponseResult<UserListResponse> getUsers(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {
        UserListResponse response = userService.getAdminUsers(page, size, keyword);
        return ResponseResult.success(response);
    }

    /**
     * 用户详情（包含购买记录和浏览记录）
     */
    @GetMapping("/{userId}")
    public ResponseResult<UserDetailResponse> getUserDetail(@PathVariable Long userId) {
        UserDetailResponse response = userService.getAdminUserDetail(userId);
        return ResponseResult.success(response);
    }
}

