package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.payment.PaymentRequest;
import com.example.OnlineShopping.dto.payment.PaymentResponse;
import com.example.OnlineShopping.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * 支付接口
     */
    @PostMapping("/{orderNo}/pay")
    public ResponseResult<PaymentResponse> pay(
            @PathVariable String orderNo,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.pay(orderNo, request);
        return ResponseResult.success("支付成功", response);
    }

    /**
     * 查询支付记录
     */
    @GetMapping("/{orderNo}")
    public ResponseResult<List<PaymentResponse>> getPaymentRecords(@PathVariable String orderNo) {
        List<PaymentResponse> response = paymentService.getPaymentRecords(orderNo);
        return ResponseResult.success(response);
    }
}

