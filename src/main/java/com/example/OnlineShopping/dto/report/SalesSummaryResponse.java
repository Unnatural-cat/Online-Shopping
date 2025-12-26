package com.example.OnlineShopping.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 销售汇总响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryResponse {
    /**
     * 订单总量
     */
    private Long totalOrders;

    /**
     * 销售额（已支付订单的总金额）
     */
    private BigDecimal totalSales;

    /**
     * 客单价（平均订单金额）
     */
    private BigDecimal averageOrderValue;

    /**
     * 支付转化率（已支付订单数 / 总订单数）
     */
    private BigDecimal conversionRate;
}

