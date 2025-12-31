package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.notification.NotificationListResponse;
import com.example.OnlineShopping.dto.notification.NotificationQueryRequest;
import com.example.OnlineShopping.service.NotificationService;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 获取用户通知列表
     */
    @GetMapping
    public ResponseResult<NotificationListResponse> getNotifications(
            @ModelAttribute NotificationQueryRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        NotificationListResponse response = notificationService.getNotifications(userId, request);
        return ResponseResult.success(response);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ResponseResult<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ResponseResult.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public ResponseResult<Void> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseResult.success();
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ResponseResult<Integer> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        int count = notificationService.getUnreadCount(userId);
        return ResponseResult.success(count);
    }
}

