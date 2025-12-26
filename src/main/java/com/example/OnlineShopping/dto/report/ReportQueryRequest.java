package com.example.OnlineShopping.dto.report;

import lombok.Data;

/**
 * 报表查询请求DTO
 */
@Data
public class ReportQueryRequest {
    /**
     * 开始时间（格式：yyyy-MM-dd）
     */
    private String startDate;

    /**
     * 结束时间（格式：yyyy-MM-dd）
     */
    private String endDate;

    /**
     * 时间粒度（day/week/month），用于销售趋势
     */
    private String granularity;

    /**
     * 热销商品TOP N的数量限制
     */
    private Integer limit;
}

