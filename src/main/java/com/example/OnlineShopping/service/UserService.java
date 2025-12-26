package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.user.AddressRequest;
import com.example.OnlineShopping.dto.user.AddressResponse;
import com.example.OnlineShopping.dto.user.ChangePasswordRequest;
import com.example.OnlineShopping.dto.user.UpdateProfileRequest;
import com.example.OnlineShopping.dto.user.UserProfileResponse;
import com.example.OnlineShopping.entity.User;
import com.example.OnlineShopping.entity.UserAddress;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.UserAddressRepository;
import com.example.OnlineShopping.repository.UserRepository;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取当前用户信息
     */
    public UserProfileResponse getCurrentUserProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在"));

        // 更新昵称
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname().trim());
        }

        user = userRepository.save(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在"));

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "原密码错误");
        }

        // 设置新密码
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 获取用户的收货地址列表
     */
    public List<AddressResponse> getUserAddresses() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        return addresses.stream()
                .map(this::convertToAddressResponse)
                .collect(Collectors.toList());
    }

    /**
     * 添加收货地址
     */
    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 如果设置为默认地址，先清除其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            userAddressRepository.clearDefaultAddress(userId);
        }

        UserAddress address = UserAddress.builder()
                .userId(userId)
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .province(request.getProvince() != null ? request.getProvince().trim() : null)
                .city(request.getCity() != null ? request.getCity().trim() : null)
                .district(request.getDistrict() != null ? request.getDistrict().trim() : null)
                .detailAddress(request.getDetailAddress().trim())
                .postalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null)
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        address = userAddressRepository.save(address);
        return convertToAddressResponse(address);
    }

    /**
     * 更新收货地址
     */
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证地址是否存在且属于当前用户
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "收货地址不存在"));

        // 如果设置为默认地址，先清除其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            userAddressRepository.clearDefaultAddress(userId);
        }

        // 更新地址信息
        address.setReceiverName(request.getReceiverName().trim());
        address.setReceiverPhone(request.getReceiverPhone().trim());
        address.setProvince(request.getProvince() != null ? request.getProvince().trim() : null);
        address.setCity(request.getCity() != null ? request.getCity().trim() : null);
        address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : null);
        address.setDetailAddress(request.getDetailAddress().trim());
        address.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        address = userAddressRepository.save(address);
        return convertToAddressResponse(address);
    }

    /**
     * 删除收货地址
     */
    @Transactional
    public void deleteAddress(Long addressId) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证地址是否存在且属于当前用户
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "收货地址不存在"));

        userAddressRepository.delete(address);
    }

    /**
     * 设置默认地址
     */
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证地址是否存在且属于当前用户
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "收货地址不存在"));

        // 清除其他默认地址
        userAddressRepository.clearDefaultAddress(userId);

        // 设置当前地址为默认
        address.setIsDefault(true);
        address = userAddressRepository.save(address);

        return convertToAddressResponse(address);
    }

    /**
     * 转换为AddressResponse
     */
    private AddressResponse convertToAddressResponse(UserAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .postalCode(address.getPostalCode())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}

