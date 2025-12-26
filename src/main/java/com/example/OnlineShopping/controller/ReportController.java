package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.report.OrderStatusDistributionResponse;
import com.example.OnlineShopping.dto.report.SalesSummaryResponse;
import com.example.OnlineShopping.dto.report.SalesTrendResponse;
import com.example.OnlineShopping.dto.report.TopProductResponse;
import com.example.OnlineShopping.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表控制器（管理端）
 */
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /**
     * 销售汇总统计
     */
    @GetMapping("/summary")
    public ResponseResult<SalesSummaryResponse> getSalesSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        SalesSummaryResponse response = reportService.getSalesSummary(startDate, endDate);
        return ResponseResult.success(response);
    }

    /**
     * 销售趋势分析
     */
    @GetMapping("/sales-trend")
    public ResponseResult<SalesTrendResponse> getSalesTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String granularity) {
        SalesTrendResponse response = reportService.getSalesTrend(startDate, endDate, granularity);
        return ResponseResult.success(response);
    }

    /**
     * 热销商品统计
     */
    @GetMapping("/top-products")
    public ResponseResult<List<TopProductResponse>> getTopProducts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<TopProductResponse> response = reportService.getTopProducts(startDate, endDate, limit);
        return ResponseResult.success(response);
    }

    /**
     * 订单状态分布统计
     */
    @GetMapping("/order-status")
    public ResponseResult<OrderStatusDistributionResponse> getOrderStatusDistribution(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        OrderStatusDistributionResponse response = reportService.getOrderStatusDistribution(startDate, endDate);
        return ResponseResult.success(response);
    }
}

