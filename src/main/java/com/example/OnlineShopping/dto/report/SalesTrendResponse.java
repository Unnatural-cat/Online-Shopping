package com.example.OnlineShopping.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 销售趋势响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTrendResponse {
    /**
     * 时间粒度（day/week/month）
     */
    private String granularity;

    /**
     * 数据点列表
     */
    private List<SalesTrendDataPoint> dataPoints;
}

