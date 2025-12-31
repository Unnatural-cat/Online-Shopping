package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据邮箱或手机号查找用户
     */
    @Query("SELECT u FROM User u WHERE u.email = :account OR u.phone = :account")
    Optional<User> findByEmailOrPhone(@Param("account") String account);

    /**
     * 根据关键词搜索用户（邮箱、手机号、昵称）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR " +
           "u.email LIKE CONCAT('%', :keyword, '%') OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%') OR " +
           "u.nickname LIKE CONCAT('%', :keyword, '%'))")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

