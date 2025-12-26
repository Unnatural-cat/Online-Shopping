package com.example.OnlineShopping.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 热销商品响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductResponse {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 销售数量
     */
    private Integer salesQuantity;

    /**
     * 销售金额
     */
    private BigDecimal salesAmount;

    /**
     * 订单数
     */
    private Long orderCount;
}

