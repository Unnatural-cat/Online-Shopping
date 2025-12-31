package com.example.OnlineShopping.dto.product;

import lombok.Data;

/**
 * 商品查询请求DTO（用于顾客端）
 */
@Data
public class ProductQueryRequest {
    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小（默认20，最大100）
     */
    private Integer size = 20;

    /**
     * 关键词（搜索商品名称或描述）
     */
    private String keyword;

    /**
     * 最低价格
     */
    private java.math.BigDecimal minPrice;

    /**
     * 最高价格
     */
    private java.math.BigDecimal maxPrice;
}

