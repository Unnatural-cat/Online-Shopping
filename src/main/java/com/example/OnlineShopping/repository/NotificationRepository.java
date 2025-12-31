package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 通知Repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * 根据用户ID分页查询通知
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和已读状态分页查询通知
     */
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    /**
     * 根据用户ID和类型分页查询通知
     */
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);

    /**
     * 根据用户ID、已读状态和类型分页查询通知
     */
    Page<Notification> findByUserIdAndIsReadAndTypeOrderByCreatedAtDesc(
            Long userId, Boolean isRead, String type, Pageable pageable);

    /**
     * 统计用户未读通知数量
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);
}
