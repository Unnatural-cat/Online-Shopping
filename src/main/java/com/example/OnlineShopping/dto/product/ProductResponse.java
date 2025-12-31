package com.example.OnlineShopping.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String status;
    private String coverImageUrl;
    private Integer salesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

