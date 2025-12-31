package com.example.OnlineShopping.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通知列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {
    private List<NotificationResponse> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private Integer unreadCount; // 未读通知数量
}

