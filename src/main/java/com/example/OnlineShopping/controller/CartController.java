package com.example.OnlineShopping.controller;

import com.example.OnlineShopping.common.ResponseResult;
import com.example.OnlineShopping.dto.cart.AddCartItemRequest;
import com.example.OnlineShopping.dto.cart.CartItemResponse;
import com.example.OnlineShopping.dto.cart.CartResponse;
import com.example.OnlineShopping.dto.cart.UpdateCartItemRequest;
import com.example.OnlineShopping.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * 获取购物车
     */
    @GetMapping
    public ResponseResult<CartResponse> getCart() {
        CartResponse response = cartService.getCart();
        return ResponseResult.success(response);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    public ResponseResult<CartItemResponse> addCartItem(@Valid @RequestBody AddCartItemRequest request) {
        CartItemResponse response = cartService.addCartItem(request);
        return ResponseResult.success("商品已添加到购物车", response);
    }

    /**
     * 更新购物车项
     */
    @PutMapping("/items/{id}")
    public ResponseResult<CartItemResponse> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartItemResponse response = cartService.updateCartItem(id, request);
        return ResponseResult.success("购物车项更新成功", response);
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/items/{id}")
    public ResponseResult<Void> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ResponseResult.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ResponseResult<Void> clearCart() {
        cartService.clearCart();
        return ResponseResult.success();
    }
}

