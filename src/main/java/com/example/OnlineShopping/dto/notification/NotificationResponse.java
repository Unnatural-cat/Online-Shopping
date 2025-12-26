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
    private String title;
    private String content;
    private String type;
    private Long relatedId;
    private String relatedOrderNo; // 关联的订单号（用于跳转）
    private Boolean isRead;
    private LocalDateTime createdAt;
}

