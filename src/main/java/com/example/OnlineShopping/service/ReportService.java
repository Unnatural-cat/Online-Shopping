package com.example.OnlineShopping.service;

import com.example.OnlineShopping.dto.report.OrderStatusDistributionResponse;
import com.example.OnlineShopping.dto.report.SalesSummaryResponse;
import com.example.OnlineShopping.dto.report.SalesTrendDataPoint;
import com.example.OnlineShopping.dto.report.SalesTrendResponse;
import com.example.OnlineShopping.dto.report.StatusCount;
import com.example.OnlineShopping.dto.report.TopProductResponse;
import com.example.OnlineShopping.enums.OrderStatus;
import com.example.OnlineShopping.repository.OrderItemRepository;
import com.example.OnlineShopping.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表服务
 */
@Service
@RequiredArgsConstructor
public class ReportService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 销售汇总统计
     */
    public SalesSummaryResponse getSalesSummary(String startDate, String endDate) {
        LocalDateTime startTime = parseStartTime(startDate);
        LocalDateTime endTime = parseEndTime(endDate);

        // 订单总量
        Long totalOrders = orderRepository.countByTimeRange(startTime, endTime);

        // 已支付订单的销售额
        BigDecimal totalSales = orderRepository.sumPaidAmountByTimeRange(startTime, endTime);
        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        // 已支付订单数
        Long paidOrders = orderRepository.countPaidOrdersByTimeRange(startTime, endTime);

        // 客单价（平均订单金额）
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (paidOrders > 0) {
            averageOrderValue = totalSales.divide(BigDecimal.valueOf(paidOrders), 2, RoundingMode.HALF_UP);
        }

        // 支付转化率
        BigDecimal conversionRate = BigDecimal.ZERO;
        if (totalOrders > 0) {
            conversionRate = BigDecimal.valueOf(paidOrders)
                    .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return SalesSummaryResponse.builder()
                .totalOrders(totalOrders)
                .totalSales(totalSales)
                .averageOrderValue(averageOrderValue)
                .conversionRate(conversionRate)
                .build();
    }

    /**
     * 销售趋势分析
     */
    public SalesTrendResponse getSalesTrend(String startDate, String endDate, String granularity) {
        LocalDateTime startTime = parseStartTime(startDate);
        LocalDateTime endTime = parseEndTime(endDate);

        String gran = (granularity != null && !granularity.trim().isEmpty()) 
                ? granularity.toLowerCase() : "day";

        List<SalesTrendDataPoint> dataPoints = new ArrayList<>();

        // 简化实现：按天统计（可按需扩展按周/月统计）
        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.plusDays(1).atStartOfDay();

            Long orderCount = orderRepository.countByTimeRange(dayStart, dayEnd);
            BigDecimal salesAmount = orderRepository.sumPaidAmountByTimeRange(dayStart, dayEnd);
            if (salesAmount == null) {
                salesAmount = BigDecimal.ZERO;
            }

            dataPoints.add(SalesTrendDataPoint.builder()
                    .timeLabel(current.format(DATE_FORMATTER))
                    .orderCount(orderCount)
                    .salesAmount(salesAmount)
                    .build());

            current = current.plusDays(1);
        }

        return SalesTrendResponse.builder()
                .granularity(gran)
                .dataPoints(dataPoints)
                .build();
    }

    /**
     * 热销商品统计（TOP N）
     */
    public List<TopProductResponse> getTopProducts(String startDate, String endDate, Integer limit) {
        LocalDateTime startTime = parseStartTime(startDate);
        LocalDateTime endTime = parseEndTime(endDate);

        int topN = (limit != null && limit > 0) ? Math.min(limit, 100) : 10; // 默认10，最大100

        List<Object[]> results = orderItemRepository.findTopProducts(startTime, endTime);

        return results.stream()
                .limit(topN)
                .map(row -> {
                    Long productId = ((Number) row[0]).longValue();
                    String productName = (String) row[1];
                    Integer salesQuantity = ((Number) row[2]).intValue();
                    BigDecimal salesAmount = (BigDecimal) row[3];
                    Long orderCount = ((Number) row[4]).longValue();

                    return TopProductResponse.builder()
                            .productId(productId)
                            .productName(productName)
                            .salesQuantity(salesQuantity)
                            .salesAmount(salesAmount)
                            .orderCount(orderCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 订单状态分布统计
     */
    public OrderStatusDistributionResponse getOrderStatusDistribution(String startDate, String endDate) {
        LocalDateTime startTime = parseStartTime(startDate);
        LocalDateTime endTime = parseEndTime(endDate);

        List<Object[]> results = orderRepository.countByStatusGroup(startTime, endTime);

        List<StatusCount> distribution = results.stream()
                .map(row -> {
                    OrderStatus status = (OrderStatus) row[0];
                    Long count = ((Number) row[1]).longValue();
                    return StatusCount.builder()
                            .status(status.name())
                            .count(count)
                            .build();
                })
                .collect(Collectors.toList());

        // 补充所有状态（即使数量为0）
        List<String> existingStatuses = distribution.stream()
                .map(StatusCount::getStatus)
                .collect(Collectors.toList());

        for (OrderStatus status : OrderStatus.values()) {
            if (!existingStatuses.contains(status.name())) {
                distribution.add(StatusCount.builder()
                        .status(status.name())
                        .count(0L)
                        .build());
            }
        }

        return OrderStatusDistributionResponse.builder()
                .distribution(distribution)
                .build();
    }

    /**
     * 解析开始时间
     */
    private LocalDateTime parseStartTime(String startDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            // 默认查询最近30天
            return LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        try {
            LocalDate date = LocalDate.parse(startDate, DATE_FORMATTER);
            return date.atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
    }

    /**
     * 解析结束时间
     */
    private LocalDateTime parseEndTime(String endDate) {
        if (endDate == null || endDate.trim().isEmpty()) {
            return LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        }
        try {
            LocalDate date = LocalDate.parse(endDate, DATE_FORMATTER);
            return date.atTime(23, 59, 59);
        } catch (Exception e) {
            return LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        }
    }
}

