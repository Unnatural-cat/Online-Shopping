package com.example.OnlineShopping.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 销售趋势数据点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTrendDataPoint {
    /**
     * 时间标签（如：2024-12-24）
     */
    private String timeLabel;

    /**
     * 订单数
     */
    private Long orderCount;

    /**
     * 销售额
     */
    private BigDecimal salesAmount;
}

