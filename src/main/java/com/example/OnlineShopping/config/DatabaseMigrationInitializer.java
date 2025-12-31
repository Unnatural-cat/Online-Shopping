package com.example.OnlineShopping.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 数据库迁移初始化器
 * 在应用启动时自动执行数据库迁移脚本
 */
@Slf4j
@Component
@Order(1) // 在其他初始化器之前执行
@RequiredArgsConstructor
public class DatabaseMigrationInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("开始检查数据库迁移...");
        
        try {
            // 检查并执行迁移：为 order_item 表添加 product_image_url 字段
            migrateAddProductImageUrlToOrderItem();
            
            log.info("数据库迁移检查完成");
        } catch (Exception e) {
            log.error("数据库迁移执行失败", e);
            // 不抛出异常，避免中断应用启动（迁移脚本已经设计为幂等的）
        }
    }

    /**
     * 迁移：为 order_item 表添加 product_image_url 字段
     */
    private void migrateAddProductImageUrlToOrderItem() {
        try {
            // 检查字段是否已存在
            boolean columnExists = checkColumnExists("order_item", "product_image_url");
            
            if (columnExists) {
                log.info("order_item.product_image_url 字段已存在，跳过迁移");
                // 即使字段已存在，也执行数据更新（幂等操作）
                updateExistingOrderItemsImageUrl();
                return;
            }

            log.info("开始执行迁移：为 order_item 表添加 product_image_url 字段");
            
            // 添加字段
            String alterTableSql = "ALTER TABLE `order_item` " +
                    "ADD COLUMN `product_image_url` VARCHAR(500) NULL COMMENT '商品图片URL（快照）' " +
                    "AFTER `product_price`";
            
            jdbcTemplate.execute(alterTableSql);
            log.info("成功添加 product_image_url 字段到 order_item 表");

            // 为现有订单项更新商品图片URL
            updateExistingOrderItemsImageUrl();
            
        } catch (Exception e) {
            // 检查是否是字段已存在的错误
            if (e.getMessage() != null && e.getMessage().contains("Duplicate column name")) {
                log.info("product_image_url 字段已存在（可能是通过其他方式创建的），跳过添加字段操作");
                updateExistingOrderItemsImageUrl();
            } else {
                log.warn("执行迁移时出现异常，继续执行数据更新操作: {}", e.getMessage());
                // 尝试继续执行数据更新
                try {
                    updateExistingOrderItemsImageUrl();
                } catch (Exception updateException) {
                    log.error("更新现有订单项图片URL失败", updateException);
                }
            }
        }
    }

    /**
     * 为现有订单项更新商品图片URL（从商品表中获取）
     */
    private void updateExistingOrderItemsImageUrl() {
        try {
            String updateSql = "UPDATE `order_item` oi " +
                    "INNER JOIN `product` p ON oi.product_id = p.id " +
                    "SET oi.product_image_url = p.cover_image_url " +
                    "WHERE oi.product_image_url IS NULL AND p.cover_image_url IS NOT NULL";
            
            int updatedRows = jdbcTemplate.update(updateSql);
            if (updatedRows > 0) {
                log.info("成功更新 {} 条订单项的图片URL", updatedRows);
            } else {
                log.debug("没有需要更新图片URL的订单项");
            }
        } catch (Exception e) {
            log.warn("更新现有订单项图片URL时出现异常: {}", e.getMessage());
            // 不抛出异常，这是可选的更新操作
        }
    }

    /**
     * 检查数据库表中是否存在指定字段
     */
    private boolean checkColumnExists(String tableName, String columnName) {
        try {
            return jdbcTemplate.execute((Connection connection) -> {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet columns = metaData.getColumns(
                        null, null, tableName, columnName);
                boolean exists = columns.next();
                columns.close();
                return exists;
            });
        } catch (Exception e) {
            log.warn("检查字段是否存在时出现异常: {}", e.getMessage());
            return false;
        }
    }
}

