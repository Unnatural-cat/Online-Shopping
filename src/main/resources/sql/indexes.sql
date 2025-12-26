-- ============================================
-- 在线购物系统数据库索引优化脚本
-- 说明：大部分索引已在建表时创建，此脚本用于补充或优化索引
-- ============================================

-- 注意：索引已在schema.sql中创建，此文件主要用于文档说明和补充索引

-- ============================================
-- 索引说明
-- ============================================

-- 1. 用户表（user）索引
--    - PRIMARY KEY: id（主键索引）
--    - UNIQUE KEY: uk_email（邮箱唯一索引）
--    - UNIQUE KEY: uk_phone（手机号唯一索引）
--    - INDEX: idx_email（邮箱普通索引，用于快速查询）
--    - INDEX: idx_phone（手机号普通索引，用于快速查询）
--    - INDEX: idx_role（角色索引，用于权限筛选）
--    - INDEX: idx_status（状态索引，用于状态筛选）

-- 2. 商品表（product）索引
--    - PRIMARY KEY: id（主键索引）
--    - INDEX: idx_category_id（分类ID索引，用于分类筛选）
--    - INDEX: idx_status（状态索引，用于状态筛选）
--    - INDEX: idx_price（价格索引，用于价格排序和筛选）
--    - INDEX: idx_sales_count（销量索引，用于销量排序）
--    - INDEX: idx_created_at（创建时间索引，用于时间排序）

-- 3. 购物车表（cart_item）索引
--    - PRIMARY KEY: id（主键索引）
--    - UNIQUE KEY: uk_user_product（用户ID+商品ID联合唯一索引，防止重复添加）
--    - INDEX: idx_user_id（用户ID索引，用于查询用户购物车）
--    - INDEX: idx_user_product（用户ID+商品ID联合索引，用于快速查找）

-- 4. 订单表（order）索引
--    - PRIMARY KEY: id（主键索引）
--    - UNIQUE KEY: uk_order_no（订单号唯一索引）
--    - INDEX: idx_order_no（订单号索引，用于快速查询）
--    - INDEX: idx_user_id（用户ID索引，用于查询用户订单）
--    - INDEX: idx_status（状态索引，用于状态筛选）
--    - INDEX: idx_created_at（创建时间索引，用于时间排序）
--    - INDEX: idx_user_status（用户ID+状态联合索引，用于查询用户特定状态订单）

-- 5. 订单项表（order_item）索引
--    - PRIMARY KEY: id（主键索引）
--    - INDEX: idx_order_id（订单ID索引，用于查询订单项）
--    - INDEX: idx_product_id（商品ID索引，用于统计商品销售）

-- 6. 支付记录表（payment_record）索引
--    - PRIMARY KEY: id（主键索引）
--    - INDEX: idx_order_id（订单ID索引，用于查询订单支付记录）
--    - INDEX: idx_transaction_no（交易流水号索引，用于查询支付记录）
--    - INDEX: idx_pay_status（支付状态索引，用于状态筛选）

-- 7. 收货地址表（user_address）索引
--    - PRIMARY KEY: id（主键索引）
--    - INDEX: idx_user_id（用户ID索引，用于查询用户地址）
--    - INDEX: idx_user_default（用户ID+默认地址联合索引，用于快速查找默认地址）

-- 8. 订单状态日志表（order_status_log）索引
--    - PRIMARY KEY: id（主键索引）
--    - INDEX: idx_order_id（订单ID索引，用于查询订单状态历史）
--    - INDEX: idx_created_at（创建时间索引，用于时间排序）

-- ============================================
-- 补充索引（如有需要可取消注释执行）
-- ============================================

-- 商品表：如果需要支持商品名称模糊查询，可以添加全文索引
-- ALTER TABLE `product` ADD FULLTEXT INDEX `ft_name_desc` (`name`, `description`);

-- 订单表：如果需要按时间范围查询，可以考虑添加复合索引
-- ALTER TABLE `order` ADD INDEX `idx_user_created` (`user_id`, `created_at`);

-- 订单项表：如果需要统计商品销售，可以考虑添加复合索引
-- ALTER TABLE `order_item` ADD INDEX `idx_product_created` (`product_id`, `created_at`);

-- ============================================
-- 索引优化建议
-- ============================================
-- 1. 定期使用 EXPLAIN 分析SQL执行计划，优化慢查询
-- 2. 根据实际业务查询场景，适当添加或删除索引
-- 3. 注意索引对INSERT/UPDATE性能的影响，不要过度索引
-- 4. 对于大表，可以考虑分区表来提高查询性能
-- 5. 定期执行 ANALYZE TABLE 更新索引统计信息

-- 查看表索引
-- SHOW INDEX FROM `user`;
-- SHOW INDEX FROM `product`;
-- SHOW INDEX FROM `cart_item`;
-- SHOW INDEX FROM `order`;
-- SHOW INDEX FROM `order_item`;
-- SHOW INDEX FROM `payment_record`;
-- SHOW INDEX FROM `user_address`;
-- SHOW INDEX FROM `order_status_log`;

