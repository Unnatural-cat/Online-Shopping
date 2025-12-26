package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.user.AddressRequest;
import com.example.OnlineShopping.dto.user.AddressResponse;
import com.example.OnlineShopping.dto.user.ChangePasswordRequest;
import com.example.OnlineShopping.dto.user.UpdateProfileRequest;
import com.example.OnlineShopping.dto.user.UserProfileResponse;
import com.example.OnlineShopping.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ResponseResult<UserProfileResponse> getProfile() {
        UserProfileResponse response = userService.getCurrentUserProfile();
        return ResponseResult.success(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ResponseResult<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(request);
        return ResponseResult.success("更新成功", response);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseResult<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseResult.success();
    }

    /**
     * 获取收货地址列表
     */
    @GetMapping("/addresses")
    public ResponseResult<List<AddressResponse>> getAddresses() {
        List<AddressResponse> addresses = userService.getUserAddresses();
        return ResponseResult.success(addresses);
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
    public ResponseResult<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        AddressResponse response = userService.addAddress(request);
        return ResponseResult.success("地址添加成功", response);
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{id}")
    public ResponseResult<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = userService.updateAddress(id, request);
        return ResponseResult.success("地址更新成功", response);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseResult<Void> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return ResponseResult.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/addresses/{id}/default")
    public ResponseResult<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        AddressResponse response = userService.setDefaultAddress(id);
        return ResponseResult.success("默认地址设置成功", response);
    }
}

