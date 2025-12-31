package com.example.OnlineShopping.service;

import com.example.OnlineShopping.dto.notification.NotificationListResponse;
import com.example.OnlineShopping.dto.notification.NotificationQueryRequest;
import com.example.OnlineShopping.dto.notification.NotificationResponse;
import com.example.OnlineShopping.entity.Notification;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.repository.NotificationRepository;
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

    /**
     * 获取用户通知列表
     */
    public NotificationListResponse getNotifications(Long userId, NotificationQueryRequest request) {
        int pageSize = Math.min(request.getSize() != null ? request.getSize() : 20, 100);
        int pageNum = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<Notification> notificationPage;

        if (request.getIsRead() != null && request.getType() != null) {
            notificationPage = notificationRepository.findByUserIdAndIsReadAndTypeOrderByCreatedAtDesc(
                    userId, request.getIsRead(), request.getType(), pageable);
        } else if (request.getIsRead() != null) {
            notificationPage = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(
                    userId, request.getIsRead(), pageable);
        } else if (request.getType() != null) {
            notificationPage = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
                    userId, request.getType(), pageable);
        } else {
            notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        int unreadCount = (int) notificationRepository.countByUserIdAndIsReadFalse(userId);

        return NotificationListResponse.builder()
                .content(notificationPage.getContent().stream()
                        .map(this::convertToNotificationResponse)
                        .collect(Collectors.toList()))
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(notificationPage.hasNext())
                .hasPrevious(notificationPage.hasPrevious())
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "通知不存在"));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此通知");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * 获取未读通知数量
     */
    public int getUnreadCount(Long userId) {
        return (int) notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 创建订单状态变更通知
     */
    @Transactional
    public void createOrderStatusNotification(Long userId, String orderNo, Long orderId, 
                                               String fromStatus, String toStatus, String remark) {
        String title = buildNotificationTitle(toStatus, orderNo);
        String content = buildNotificationContent(orderNo, fromStatus, toStatus, remark);
        String link = "/orders/" + orderNo;

        Notification notification = Notification.builder()
                .userId(userId)
                .type("ORDER_STATUS")
                .title(title)
                .content(content)
                .orderNo(orderNo)
                .orderId(orderId)
                .isRead(false)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 创建订单发货通知
     */
    @Transactional
    public void createOrderShippedNotification(Long userId, String orderNo, Long orderId, String trackingNumber) {
        String title = "您的订单已发货";
        String content = String.format("订单号：%s 已发货", orderNo);
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            content += "，物流单号：" + trackingNumber;
        }
        String link = "/orders/" + orderNo;

        Notification notification = Notification.builder()
                .userId(userId)
                .type("ORDER_SHIPPED")
                .title(title)
                .content(content)
                .orderNo(orderNo)
                .orderId(orderId)
                .isRead(false)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 转换为NotificationResponse
     */
    private NotificationResponse convertToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .orderNo(notification.getOrderNo())
                .orderId(notification.getOrderId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .link(notification.getLink())
                .build();
    }

    /**
     * 构建通知标题
     */
    private String buildNotificationTitle(String status, String orderNo) {
        String statusText = getStatusText(status);
        return String.format("订单%s：%s", orderNo, statusText);
    }

    /**
     * 构建通知内容
     */
    private String buildNotificationContent(String orderNo, String fromStatus, String toStatus, String remark) {
        String fromText = getStatusText(fromStatus);
        String toText = getStatusText(toStatus);
        String content = String.format("订单号：%s 状态已从【%s】变更为【%s】", orderNo, fromText, toText);
        if (remark != null && !remark.trim().isEmpty()) {
            content += "，" + remark;
        }
        return content;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "CREATED": return "待支付";
            case "PAID": return "待发货";
            case "SHIPPED": return "已发货";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            case "REFUNDED": return "已退款";
            default: return status;
        }
    }
}

