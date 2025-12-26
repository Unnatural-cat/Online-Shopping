package com.example.OnlineShopping.repository;

import com.example.OnlineShopping.entity.Product;
import com.example.OnlineShopping.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 商品Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * 根据状态分页查询商品（用于顾客端，只显示上架商品）
     */
    Page<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status, Pageable pageable);

    /**
     * 根据分类ID和状态分页查询商品
     */
    Page<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status, Pageable pageable);

    /**
     * 根据关键词搜索商品（名称或描述中包含关键词，且状态为上架）
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, 
                                   @Param("status") ProductStatus status, 
                                   Pageable pageable);

    /**
     * 根据价格区间和状态查询商品
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
           "p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      @Param("status") ProductStatus status,
                                      Pageable pageable);

    /**
     * 组合查询：分类、价格区间、关键词、状态
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "p.status = :status AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findWithFilters(@Param("categoryId") Long categoryId,
                                   @Param("status") ProductStatus status,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    /**
     * 管理端：查询所有商品（包括下架和已删除，按创建时间倒序）
     */
    Page<Product> findByStatusNotOrderByCreatedAtDesc(ProductStatus status, Pageable pageable);

    /**
     * 根据ID查找商品（包括已删除的，用于管理端）
     */
    Optional<Product> findById(Long id);
}

