package com.example.OnlineShopping.entity;

import com.example.OnlineShopping.enums.PaymentMethod;
import com.example.OnlineShopping.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Entity
@Table(name = "payment_record", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_transaction_no", columnList = "transaction_no"),
    @Index(name = "idx_pay_status", columnList = "pay_status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus payStatus = PaymentStatus.INIT;

    @Column(name = "transaction_no", length = 64)
    private String transactionNo;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

