package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.payment.PaymentRequest;
import com.example.OnlineShopping.dto.payment.PaymentResponse;
import com.example.OnlineShopping.entity.Order;
import com.example.OnlineShopping.entity.PaymentRecord;
import com.example.OnlineShopping.enums.OrderStatus;
import com.example.OnlineShopping.enums.PaymentMethod;
import com.example.OnlineShopping.enums.PaymentStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.OrderRepository;
import com.example.OnlineShopping.repository.PaymentRecordRepository;
import com.example.OnlineShopping.util.SecurityUtil;
import com.example.OnlineShopping.util.TransactionNoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRecordRepository paymentRecordRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * 模拟支付
     */
    @Transactional
    public PaymentResponse pay(String orderNo, PaymentRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 查找订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该订单");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单状态不正确，无法支付");
        }

        // 解析支付方式
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "支付方式不正确");
        }

        // 生成交易流水号
        String transactionNo = TransactionNoUtil.generateTransactionNo();

        // 创建支付记录（初始状态为INIT）
        PaymentRecord paymentRecord = PaymentRecord.builder()
                .orderId(order.getId())
                .paymentMethod(paymentMethod)
                .payStatus(PaymentStatus.INIT)
                .transactionNo(transactionNo)
                .build();
        paymentRecord = paymentRecordRepository.save(paymentRecord);

        // 模拟支付（实际支付逻辑，这里直接设置为成功）
        // 在真实场景中，这里会调用第三方支付接口
        log.info("模拟支付：订单号={}, 交易流水号={}, 支付方式={}", orderNo, transactionNo, paymentMethod);

        // 更新支付记录为成功
        paymentRecord.setPayStatus(PaymentStatus.SUCCESS);
        paymentRecord.setPaidAt(LocalDateTime.now());
        paymentRecord = paymentRecordRepository.save(paymentRecord);

        // 更新订单状态为已支付（会扣减库存）
        orderService.payOrder(orderNo);

        return convertToPaymentResponse(paymentRecord, orderNo);
    }

    /**
     * 查询支付记录
     */
    public List<PaymentResponse> getPaymentRecords(String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 查找订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该订单的支付记录");
        }

        // 查询支付记录
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findByOrderIdOrderByCreatedAtDesc(order.getId());

        return paymentRecords.stream()
                .map(record -> convertToPaymentResponse(record, orderNo))
                .collect(Collectors.toList());
    }

    /**
     * 转换为PaymentResponse
     */
    private PaymentResponse convertToPaymentResponse(PaymentRecord paymentRecord, String orderNo) {
        return PaymentResponse.builder()
                .id(paymentRecord.getId())
                .orderId(paymentRecord.getOrderId())
                .orderNo(orderNo)
                .paymentMethod(paymentRecord.getPaymentMethod().name())
                .payStatus(paymentRecord.getPayStatus().name())
                .transactionNo(paymentRecord.getTransactionNo())
                .paidAt(paymentRecord.getPaidAt())
                .createdAt(paymentRecord.getCreatedAt())
                .build();
    }
}

