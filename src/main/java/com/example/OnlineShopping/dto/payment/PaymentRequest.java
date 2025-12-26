package com.example.OnlineShopping.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付请求DTO
 */
@Data
public class PaymentRequest {
    /**
     * 支付方式（MOCK/ALIPAY/WECHAT）
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}

