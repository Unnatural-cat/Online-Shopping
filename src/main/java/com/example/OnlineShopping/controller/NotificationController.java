package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.notification.NotificationListResponse;
import com.example.OnlineShopping.service.NotificationService;
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
     * 获取通知列表
     */
    @GetMapping
    public ResponseResult<NotificationListResponse> getNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Boolean isRead) {
        NotificationListResponse response = notificationService.getNotifications(page, size, isRead);
        return ResponseResult.success(response);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ResponseResult<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseResult.success("已标记为已读");
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public ResponseResult<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseResult.success("已全部标记为已读");
    }
}

