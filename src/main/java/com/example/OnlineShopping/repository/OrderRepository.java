package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.Order;
import com.example.OnlineShopping.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID分页查询订单
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态分页查询订单
     */
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status, Pageable pageable);

    /**
     * 管理端：根据条件查询订单（多条件筛选）
     */
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNo IS NULL OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) AND " +
           "(:userId IS NULL OR o.userId = :userId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findWithFilters(@Param("orderNo") String orderNo,
                                 @Param("userId") Long userId,
                                 @Param("status") OrderStatus status,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime,
                                 Pageable pageable);

    /**
     * 统计指定时间范围内的订单总数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime)")
    Long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内已支付订单的总销售额
     */
    @Query("SELECT COALESCE(SUM(o.payAmount), 0) FROM Order o WHERE " +
           "o.status IN ('PAID', 'SHIPPED', 'COMPLETED') AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime)")
    BigDecimal sumPaidAmountByTimeRange(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内已支付订单数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE " +
           "o.status IN ('PAID', 'SHIPPED', 'COMPLETED') AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime)")
    Long countPaidOrdersByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 按状态分组统计订单数量
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime) " +
           "GROUP BY o.status")
    List<Object[]> countByStatusGroup(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);
}
