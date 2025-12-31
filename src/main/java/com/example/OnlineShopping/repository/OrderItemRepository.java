package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单项Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * 根据订单ID查找所有订单项
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 统计热销商品（TOP N）
     * 按商品ID分组，使用商品表中的最新名称，避免同一商品因名称不同而重复
     */
    @Query("SELECT oi.productId, COALESCE(MAX(p.name), MAX(oi.productName)) as productName, " +
           "SUM(oi.quantity) as salesQuantity, " +
           "SUM(oi.subtotalAmount) as salesAmount, " +
           "COUNT(DISTINCT oi.orderId) as orderCount " +
           "FROM OrderItem oi " +
           "INNER JOIN Order o ON oi.orderId = o.id " +
           "LEFT JOIN Product p ON oi.productId = p.id " +
           "WHERE o.status IN ('PAID', 'SHIPPED', 'COMPLETED') AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime) " +
           "GROUP BY oi.productId " +
           "ORDER BY salesQuantity DESC")
    List<Object[]> findTopProducts(@Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}
