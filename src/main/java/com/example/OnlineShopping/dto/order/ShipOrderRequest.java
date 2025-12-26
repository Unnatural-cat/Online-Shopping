package com.example.OnlineShopping.dto.order;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 订单发货请求DTO
 */
@Data
public class ShipOrderRequest {
    /**
     * 物流单号（可选）
     */
    @Size(max = 100, message = "物流单号长度不能超过100个字符")
    private String trackingNumber;

    /**
     * 备注（可选）
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}

