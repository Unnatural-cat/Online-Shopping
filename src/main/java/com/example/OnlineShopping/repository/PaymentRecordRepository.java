package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支付记录Repository
 */
@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    /**
     * 根据订单ID查找支付记录（按创建时间倒序）
     */
    List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    /**
     * 根据订单ID查找最新的支付记录
     */
    Optional<PaymentRecord> findFirstByOrderIdOrderByCreatedAtDesc(Long orderId);

    /**
     * 根据交易流水号查找支付记录
     */
    Optional<PaymentRecord> findByTransactionNo(String transactionNo);
}

