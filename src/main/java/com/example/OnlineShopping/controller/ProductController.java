package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.product.ProductListResponse;
import com.example.OnlineShopping.dto.product.ProductQueryRequest;
import com.example.OnlineShopping.dto.product.ProductResponse;
import com.example.OnlineShopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器（顾客端）
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * 商品列表（分页、搜索、筛选）
     */
    @GetMapping
    public ResponseResult<ProductListResponse> getProducts(@ModelAttribute ProductQueryRequest request) {
        ProductListResponse response = productService.getProducts(request);
        return ResponseResult.success(response);
    }

    /**
     * 商品详情
     */
    @GetMapping("/{id}")
    public ResponseResult<ProductResponse> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetail(id);
        return ResponseResult.success(response);
    }
}

