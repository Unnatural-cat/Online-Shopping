package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.product.CreateProductRequest;
import com.example.OnlineShopping.dto.product.ProductListResponse;
import com.example.OnlineShopping.dto.product.ProductQueryRequest;
import com.example.OnlineShopping.dto.product.ProductResponse;
import com.example.OnlineShopping.dto.product.UpdateProductRequest;
import com.example.OnlineShopping.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理控制器（管理端）
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    /**
     * 商品列表（管理端）
     */
    @GetMapping
    public ResponseResult<ProductListResponse> getProducts(@ModelAttribute ProductQueryRequest request) {
        ProductListResponse response = productService.getAdminProducts(request);
        return ResponseResult.success(response);
    }

    /**
     * 商品详情（管理端）
     */
    @GetMapping("/{id}")
    public ResponseResult<ProductResponse> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetail(id);
        return ResponseResult.success(response);
    }

    /**
     * 创建商品
     */
    @PostMapping
    public ResponseResult<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseResult.success("商品创建成功", response);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public ResponseResult<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseResult.success("商品更新成功", response);
    }

    /**
     * 删除商品（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseResult.success();
    }

    /**
     * 商品上下架
     */
    @PutMapping("/{id}/status")
    public ResponseResult<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> request) {
        String status = request.get("status");
        if (status == null || status.isEmpty()) {
            throw new com.example.OnlineShopping.exception.BusinessException(
                    com.example.OnlineShopping.common.ErrorCode.PARAM_ERROR, "状态参数不能为空");
        }
        ProductResponse response = productService.updateProductStatus(id, status);
        return ResponseResult.success("商品状态更新成功", response);
    }
}

