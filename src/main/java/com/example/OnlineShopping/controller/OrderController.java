package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.order.CreateOrderRequest;
import com.example.OnlineShopping.dto.order.OrderListResponse;
import com.example.OnlineShopping.dto.order.OrderQueryRequest;
import com.example.OnlineShopping.dto.order.OrderResponse;
import com.example.OnlineShopping.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（顾客端）
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseResult<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseResult.success("订单创建成功", response);
    }

    /**
     * 订单列表
     */
    @GetMapping
    public ResponseResult<OrderListResponse> getOrders(@ModelAttribute OrderQueryRequest request) {
        OrderListResponse response = orderService.getOrders(request);
        return ResponseResult.success(response);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{orderNo}")
    public ResponseResult<OrderResponse> getOrderDetail(@PathVariable String orderNo) {
        OrderResponse response = orderService.getOrderDetail(orderNo);
        return ResponseResult.success(response);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderNo}/cancel")
    public ResponseResult<OrderResponse> cancelOrder(@PathVariable String orderNo) {
        OrderResponse response = orderService.cancelOrder(orderNo);
        return ResponseResult.success("订单取消成功", response);
    }
}

