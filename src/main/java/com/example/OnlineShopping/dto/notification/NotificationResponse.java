package com.example.OnlineShopping.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String type; // ORDER_STATUS, ORDER_SHIPPED, etc.
    private String title;
    private String content;
    private String orderNo;
    private Long orderId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String link; // 跳转链接
}

