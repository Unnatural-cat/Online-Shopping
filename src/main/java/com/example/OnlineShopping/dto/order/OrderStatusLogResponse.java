package com.example.OnlineShopping.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单状态日志响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusLogResponse {
    private Long id;
    private String fromStatus;
    private String toStatus;
    private String remark;
    private String operator;
    private LocalDateTime createdAt;
}

