package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户收货地址Repository
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    /**
     * 根据用户ID查找所有地址
     */
    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和地址ID查找地址（用于验证地址是否属于该用户）
     */
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    /**
     * 根据用户ID查找默认地址
     */
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 取消用户的所有默认地址
     */
    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.userId = :userId")
    void clearDefaultAddress(@Param("userId") Long userId);
}

