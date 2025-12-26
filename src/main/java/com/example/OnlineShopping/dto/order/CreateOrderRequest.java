package com.example.OnlineShopping.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    /**
     * 购物车项ID列表（需要勾选的购物车项）
     */
    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartItemIds;

    /**
     * 收货地址ID（可选，如果提供则使用地址ID，否则使用下面的地址信息）
     */
    private Long addressId;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Size(max = 20, message = "收货人电话长度不能超过20个字符")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    @Size(max = 500, message = "收货地址长度不能超过500个字符")
    private String receiverAddress;
}

