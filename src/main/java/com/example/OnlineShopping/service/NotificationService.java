package com.example.OnlineShopping.service;

import com.example.OnlineShopping.dto.notification.NotificationListResponse;
import com.example.OnlineShopping.dto.notification.NotificationResponse;
import com.example.OnlineShopping.entity.Notification;
import com.example.OnlineShopping.repository.NotificationRepository;
import com.example.OnlineShopping.repository.OrderRepository;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 通知服务
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;

    /**
     * 获取通知列表
     */
    public NotificationListResponse getNotifications(Integer page, Integer size, Boolean isRead) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Notification> notificationPage;
        if (isRead != null) {
            notificationPage = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, isRead, pageable);
        } else {
            notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        return NotificationListResponse.builder()
                .items(notificationPage.getContent().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()))
                .total(notificationPage.getTotalElements())
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        int updated = notificationRepository.markAsRead(id, userId);
        if (updated == 0) {
            throw new RuntimeException("通知不存在或无权限");
        }
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * 创建通知
     */
    @Transactional
    public Notification createNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    /**
     * 转换为响应DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        String relatedOrderNo = null;
        // 如果是订单相关通知，查询订单号
        if (notification.getRelatedId() != null && 
            (notification.getType() != null && notification.getType().startsWith("ORDER_"))) {
            orderRepository.findById(notification.getRelatedId())
                    .ifPresent(order -> relatedOrderNo = order.getOrderNo());
        }
        
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .relatedId(notification.getRelatedId())
                .relatedOrderNo(relatedOrderNo)
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

