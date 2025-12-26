package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.cart.AddCartItemRequest;
import com.example.OnlineShopping.dto.cart.CartItemResponse;
import com.example.OnlineShopping.dto.cart.CartResponse;
import com.example.OnlineShopping.dto.cart.UpdateCartItemRequest;
import com.example.OnlineShopping.entity.CartItem;
import com.example.OnlineShopping.entity.Product;
import com.example.OnlineShopping.enums.ProductStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.CartItemRepository;
import com.example.OnlineShopping.repository.ProductRepository;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务
 */
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * 获取购物车列表
     */
    public CartResponse getCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<CartItemResponse> items = cartItems.stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);
                    return convertToCartItemResponse(item, product);
                })
                .filter(item -> item != null) // 过滤掉商品已删除的购物车项
                .collect(Collectors.toList());

        // 计算总数量和总金额
        int totalCount = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal checkedAmount = items.stream()
                .filter(CartItemResponse::getChecked)
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalCount(totalCount)
                .totalAmount(totalAmount)
                .checkedAmount(checkedAmount)
                .build();
    }

    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartItemResponse addCartItem(AddCartItemRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证商品是否存在且上架
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品已下架，无法添加到购物车");
        }

        // 验证库存
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品库存不足");
        }

        // 检查购物车中是否已有该商品
        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        CartItem cartItem;
        if (existingItem != null) {
            // 如果已存在，增加数量
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "商品库存不足");
            }
            existingItem.setQuantity(newQuantity);
            cartItem = cartItemRepository.save(existingItem);
        } else {
            // 如果不存在，创建新的购物车项
            cartItem = CartItem.builder()
                    .userId(userId)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .checked(false)
                    .build();
            cartItem = cartItemRepository.save(cartItem);
        }

        return convertToCartItemResponse(cartItem, product);
    }

    /**
     * 更新购物车项
     */
    @Transactional
    public CartItemResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证购物车项是否存在且属于当前用户
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "购物车项不存在"));

        // 验证商品是否存在且上架
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品已下架");
        }

        // 更新数量
        if (request.getQuantity() != null) {
            if (product.getStock() < request.getQuantity()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "商品库存不足");
            }
            cartItem.setQuantity(request.getQuantity());
        }

        // 更新勾选状态
        if (request.getChecked() != null) {
            cartItem.setChecked(request.getChecked());
        }

        cartItem = cartItemRepository.save(cartItem);
        return convertToCartItemResponse(cartItem, product);
    }

    /**
     * 删除购物车项
     */
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证购物车项是否存在且属于当前用户
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "购物车项不存在"));

        cartItemRepository.delete(cartItem);
    }

    /**
     * 清空购物车
     */
    @Transactional
    public void clearCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        cartItemRepository.deleteByUserId(userId);
    }

    /**
     * 校验购物车商品（库存、上架状态）
     */
    public void validateCartItems(List<Long> cartItemIds) {
        Long userId = SecurityUtil.getCurrentUserId();

        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "购物车项不存在"));

            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

            if (product.getStatus() != ProductStatus.ON_SALE) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, 
                        String.format("商品【%s】已下架，无法结算", product.getName()));
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, 
                        String.format("商品【%s】库存不足，当前库存：%d，购买数量：%d", 
                                product.getName(), product.getStock(), cartItem.getQuantity()));
            }
        }
    }

    /**
     * 转换为CartItemResponse
     */
    private CartItemResponse convertToCartItemResponse(CartItem cartItem, Product product) {
        if (product == null) {
            return null; // 商品已删除
        }

        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getCoverImageUrl())
                .productPrice(product.getPrice())
                .stock(product.getStock())
                .quantity(cartItem.getQuantity())
                .checked(cartItem.getChecked())
                .subtotal(subtotal)
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }
}

