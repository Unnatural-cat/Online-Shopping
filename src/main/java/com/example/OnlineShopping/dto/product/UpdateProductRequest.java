package com.example.OnlineShopping.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新商品请求DTO
 */
@Data
public class UpdateProductRequest {
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String name;

    private Long categoryId;

    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock;

    @Size(max = 5000, message = "商品描述长度不能超过5000个字符")
    private String description;

    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImageUrl;

    private String status; // ON_SALE/OFF_SALE
}

