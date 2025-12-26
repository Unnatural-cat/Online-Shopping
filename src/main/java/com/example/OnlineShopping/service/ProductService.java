package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.product.CreateProductRequest;
import com.example.OnlineShopping.dto.product.ProductListResponse;
import com.example.OnlineShopping.dto.product.ProductQueryRequest;
import com.example.OnlineShopping.dto.product.ProductResponse;
import com.example.OnlineShopping.dto.product.UpdateProductRequest;
import com.example.OnlineShopping.entity.Product;
import com.example.OnlineShopping.enums.ProductStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * 顾客端：查询商品列表（分页、搜索、筛选）
     */
    public ProductListResponse getProducts(ProductQueryRequest request) {
        // 限制每页最大100条
        int pageSize = Math.min(request.getSize() != null ? request.getSize() : 20, 100);
        int pageNum = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<Product> productPage;

        // 如果有关键词或价格区间，使用组合查询
        if ((request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) ||
            request.getMinPrice() != null || request.getMaxPrice() != null ||
            request.getCategoryId() != null) {
            productPage = productRepository.findWithFilters(
                request.getCategoryId(),
                ProductStatus.ON_SALE,
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getKeyword() != null ? request.getKeyword().trim() : null,
                pageable
            );
        } else {
            // 简单查询：只查询上架商品
            productPage = productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.ON_SALE, pageable);
        }

        return ProductListResponse.builder()
                .content(productPage.getContent().stream()
                        .map(this::convertToProductResponse)
                        .collect(Collectors.toList()))
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }

    /**
     * 顾客端：查询商品详情
     */
    public ProductResponse getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        // 顾客端只能查看上架商品
        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在或已下架");
        }

        return convertToProductResponse(product);
    }

    /**
     * 管理端：创建商品
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName().trim())
                .categoryId(request.getCategoryId())
                .price(request.getPrice())
                .stock(request.getStock())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .coverImageUrl(request.getCoverImageUrl() != null ? request.getCoverImageUrl().trim() : null)
                .status(parseProductStatus(request.getStatus()))
                .build();

        product = productRepository.save(product);
        return convertToProductResponse(product);
    }

    /**
     * 管理端：更新商品
     */
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        // 已删除的商品不能更新
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已删除的商品不能更新");
        }

        // 更新字段
        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }
        if (request.getCoverImageUrl() != null) {
            product.setCoverImageUrl(request.getCoverImageUrl().trim());
        }
        if (request.getStatus() != null) {
            product.setStatus(parseProductStatus(request.getStatus()));
        }

        product = productRepository.save(product);
        return convertToProductResponse(product);
    }

    /**
     * 管理端：删除商品（逻辑删除）
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    /**
     * 管理端：商品上下架
     */
    @Transactional
    public ProductResponse updateProductStatus(Long id, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已删除的商品不能上下架");
        }

        product.setStatus(parseProductStatus(status));
        product = productRepository.save(product);
        return convertToProductResponse(product);
    }

    /**
     * 管理端：查询商品列表（包括下架商品）
     */
    public ProductListResponse getAdminProducts(ProductQueryRequest request) {
        int pageSize = Math.min(request.getSize() != null ? request.getSize() : 20, 100);
        int pageNum = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // 管理端查询所有未删除的商品
        Page<Product> productPage = productRepository.findByStatusNotOrderByCreatedAtDesc(
                ProductStatus.DELETED, pageable);

        return ProductListResponse.builder()
                .content(productPage.getContent().stream()
                        .map(this::convertToProductResponse)
                        .collect(Collectors.toList()))
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }

    /**
     * 解析商品状态
     */
    private ProductStatus parseProductStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ProductStatus.OFF_SALE;
        }
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProductStatus.OFF_SALE;
        }
    }

    /**
     * 转换为ProductResponse
     */
    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .status(product.getStatus().name())
                .coverImageUrl(product.getCoverImageUrl())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

