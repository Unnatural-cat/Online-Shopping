package com.example.OnlineShopping.dto.order;

import lombok.Data;

/**
 * 订单查询请求DTO
 */
@Data
public class OrderQueryRequest {
    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小（默认20，最大100）
     */
    private Integer size = 20;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单号（管理端）
     */
    private String orderNo;

    /**
     * 用户ID（管理端）
     */
    private Long userId;

    /**
     * 开始时间（管理端）
     */
    private String startTime;

    /**
     * 结束时间（管理端）
     */
    private String endTime;
}

