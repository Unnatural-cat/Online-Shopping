package com.example.OnlineShopping.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNo;
    private String paymentMethod;
    private String payStatus;
    private String transactionNo;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}

