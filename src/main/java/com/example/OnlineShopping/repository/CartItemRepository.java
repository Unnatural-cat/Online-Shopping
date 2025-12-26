package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车Repository
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * 根据用户ID查找所有购物车项
     */
    List<CartItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和商品ID查找购物车项（用于检查商品是否已在购物车中）
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户ID和购物车项ID查找（用于验证购物车项是否属于该用户）
     */
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    /**
     * 删除用户的所有购物车项
     */
    void deleteByUserId(Long userId);
}

