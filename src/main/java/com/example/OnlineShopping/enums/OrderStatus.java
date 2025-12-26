package com.example.OnlineShopping.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    CREATED,    // 已创建/待支付
    PAID,       // 已支付/待发货
    SHIPPED,    // 已发货
    COMPLETED,  // 已完成
    CANCELLED,  // 已取消
    REFUNDED    // 已退款（可选）
}

