package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.order.OrderResponse;
import com.example.OnlineShopping.dto.user.AddressRequest;
import com.example.OnlineShopping.dto.user.AddressResponse;
import com.example.OnlineShopping.dto.user.ChangePasswordRequest;
import com.example.OnlineShopping.dto.user.UpdateProfileRequest;
import com.example.OnlineShopping.dto.user.UserDetailResponse;
import com.example.OnlineShopping.dto.user.UserListResponse;
import com.example.OnlineShopping.dto.user.UserProfileResponse;
import com.example.OnlineShopping.entity.Order;
import com.example.OnlineShopping.entity.OrderItem;
import com.example.OnlineShopping.entity.User;
import com.example.OnlineShopping.entity.UserAddress;
import com.example.OnlineShopping.enums.OrderStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.OrderItemRepository;
import com.example.OnlineShopping.repository.OrderRepository;
import com.example.OnlineShopping.repository.UserAddressRepository;
import com.example.OnlineShopping.repository.UserRepository;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
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
     * 管理端：获取用户列表
     */
    public UserListResponse getAdminUsers(Integer page, Integer size, String keyword) {
        int pageSize = Math.min(size != null ? size : 20, 100);
        int pageNum = page != null && page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 根据关键词搜索（邮箱、手机号、昵称）
            userPage = userRepository.findByKeyword(keyword.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserListResponse.UserListItem> items = userPage.getContent().stream()
                .map(this::convertToUserListItem)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .content(items)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    /**
     * 管理端：获取用户详情（包含购买记录和浏览记录）
     */
    public UserDetailResponse getAdminUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));

        // 获取用户订单统计
        List<Order> allOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getContent();
        long orderCount = allOrders.size();
        long completedOrderCount = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .count();
        BigDecimal totalSpent = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || 
                            o.getStatus() == OrderStatus.SHIPPED || 
                            o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 获取最近的订单（购买记录）
        List<Order> recentOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(0, 20)).getContent();
        List<OrderResponse> orderResponses = recentOrders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());

        // 获取浏览记录（从订单商品中推断）
        List<UserDetailResponse.BrowseRecord> browseRecords = getBrowseRecords(userId);

        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .orderCount(orderCount)
                .totalSpent(totalSpent.multiply(new BigDecimal("100")).longValue()) // 转换为分
                .completedOrderCount(completedOrderCount)
                .orders(orderResponses)
                .browseRecords(browseRecords)
                .build();
    }

    /**
     * 获取用户的浏览记录（从订单商品中推断）
     */
    private List<UserDetailResponse.BrowseRecord> getBrowseRecords(Long userId) {
        // 获取用户的所有订单
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(
                userId, Pageable.unpaged()).getContent();

        // 统计每个商品的浏览次数和最后浏览时间
        Map<Long, UserDetailResponse.BrowseRecord> recordMap = new HashMap<>();
        
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                Long productId = item.getProductId();
                if (recordMap.containsKey(productId)) {
                    UserDetailResponse.BrowseRecord record = recordMap.get(productId);
                    record.setViewCount(record.getViewCount() + item.getQuantity());
                    // 更新最后浏览时间为更晚的时间
                    if (order.getCreatedAt().isAfter(record.getLastViewTime())) {
                        record.setLastViewTime(order.getCreatedAt());
                    }
                } else {
                    recordMap.put(productId, UserDetailResponse.BrowseRecord.builder()
                            .productId(productId)
                            .productName(item.getProductName())
                            .productImageUrl(item.getProductImageUrl())
                            .viewCount(item.getQuantity())
                            .lastViewTime(order.getCreatedAt())
                            .build());
                }
            }
        }

        // 按最后浏览时间倒序排列
        return recordMap.values().stream()
                .sorted((a, b) -> b.getLastViewTime().compareTo(a.getLastViewTime()))
                .limit(20) // 最多显示20条
                .collect(Collectors.toList());
    }

    /**
     * 转换为UserListItem
     */
    private UserListResponse.UserListItem convertToUserListItem(User user) {
        // 获取用户订单统计
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId(), Pageable.unpaged()).getContent();
        long orderCount = orders.size();
        BigDecimal totalSpent = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || 
                            o.getStatus() == OrderStatus.SHIPPED || 
                            o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return UserListResponse.UserListItem.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .orderCount(orderCount)
                .totalSpent(totalSpent.multiply(new BigDecimal("100")).longValue()) // 转换为分
                .build();
    }

    /**
     * 转换为OrderResponse（简化版，用于用户详情）
     */
    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<com.example.OnlineShopping.dto.order.OrderItemResponse> itemResponses = items.stream()
                .map(item -> com.example.OnlineShopping.dto.order.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImageUrl(item.getProductImageUrl())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .subtotalAmount(item.getSubtotalAmount())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .paidAt(order.getPaidAt())
                .shippedAt(order.getShippedAt())
                .completedAt(order.getCompletedAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
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

