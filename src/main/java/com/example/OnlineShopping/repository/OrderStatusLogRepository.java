package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单状态日志Repository
 */
@Repository
public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, Long> {
    /**
     * 根据订单ID查找所有状态日志（按创建时间倒序）
     */
    List<OrderStatusLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}

