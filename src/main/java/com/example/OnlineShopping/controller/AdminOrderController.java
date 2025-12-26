package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.order.OrderListResponse;
import com.example.OnlineShopping.dto.order.OrderQueryRequest;
import com.example.OnlineShopping.dto.order.OrderResponse;
import com.example.OnlineShopping.dto.order.ShipOrderRequest;
import com.example.OnlineShopping.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器（管理端）
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    /**
     * 订单列表
     */
    @GetMapping
    public ResponseResult<OrderListResponse> getOrders(@ModelAttribute OrderQueryRequest request) {
        OrderListResponse response = orderService.getAdminOrders(request);
        return ResponseResult.success(response);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{orderNo}")
    public ResponseResult<OrderResponse> getOrderDetail(@PathVariable String orderNo) {
        OrderResponse response = orderService.getAdminOrderDetail(orderNo);
        return ResponseResult.success(response);
    }

    /**
     * 订单发货
     */
    @PutMapping("/{orderNo}/ship")
    public ResponseResult<OrderResponse> shipOrder(
            @PathVariable String orderNo,
            @Valid @RequestBody ShipOrderRequest request) {
        OrderResponse response = orderService.shipOrder(orderNo, request);
        return ResponseResult.success("订单发货成功", response);
    }
}

