package com.example.OnlineShopping.dto.notification;

import lombok.Data;

/**
 * 通知查询请求DTO
 */
@Data
public class NotificationQueryRequest {
    private Integer page = 1;
    private Integer size = 20;
    private Boolean isRead; // 是否已读，null表示全部
    private String type; // 通知类型，null表示全部
}

