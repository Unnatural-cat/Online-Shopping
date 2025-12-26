package com.example.OnlineShopping.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 更新购物车项请求DTO
 */
@Data
public class UpdateCartItemRequest {
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;

    private Boolean checked;
}

