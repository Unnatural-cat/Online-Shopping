-- ============================================
-- 在线购物系统初始化数据脚本
-- 用于开发和测试环境
-- ============================================

-- 注意：密码使用BCrypt加密
-- 重要提示：以下密码哈希值为示例，实际使用时需要通过注册接口或程序生成
-- 推荐的BCrypt哈希值（明文密码：password123）：
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKvKx9u
-- 
-- 生成BCrypt哈希的方法：
-- 1. 通过注册接口注册新用户（推荐）
-- 2. 使用Java代码：BCryptPasswordEncoder.encode("password")
-- 3. 使用在线BCrypt工具：https://bcrypt-generator.com/

-- ============================================
-- 1. 初始化用户数据
-- ============================================

-- 管理员账号（邮箱：admin@example.com，密码：password123）
-- 测试用户（密码：password123）
-- 注意：如果密码哈希值不正确，请通过注册接口重新注册或使用程序生成正确的哈希值
INSERT INTO `user` (`email`, `phone`, `password_hash`, `nickname`, `role`, `status`, `created_at`, `updated_at`)
VALUES 
('admin@example.com', '13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKvKx9u', '管理员', 'ADMIN', 'NORMAL', NOW(), NOW()),
('customer1@example.com', '13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKvKx9u', '测试用户1', 'CUSTOMER', 'NORMAL', NOW(), NOW()),
('customer2@example.com', '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKvKx9u', '测试用户2', 'CUSTOMER', 'NORMAL', NOW(), NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- ============================================
-- 2. 初始化商品数据
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
VALUES
('iPhone 15 Pro Max 256GB', 1, 8999.00, 100, 'Apple iPhone 15 Pro Max，256GB存储，钛金属原色', 'ON_SALE', 'https://example.com/images/iphone15.jpg', 0, NOW(), NOW()),
('MacBook Pro 14英寸 M3', 2, 15999.00, 50, 'Apple MacBook Pro 14英寸，M3芯片，16GB内存，512GB SSD', 'ON_SALE', 'https://example.com/images/macbook.jpg', 0, NOW(), NOW()),
('AirPods Pro (第2代)', 3, 1899.00, 200, 'Apple AirPods Pro (第2代)，主动降噪，MagSafe充电盒', 'ON_SALE', 'https://example.com/images/airpods.jpg', 0, NOW(), NOW()),
('iPad Air 11英寸', 4, 4799.00, 80, 'Apple iPad Air 11英寸，M2芯片，256GB存储', 'ON_SALE', 'https://example.com/images/ipad.jpg', 0, NOW(), NOW()),
('Apple Watch Series 9', 5, 2999.00, 150, 'Apple Watch Series 9，45mm，GPS版本', 'ON_SALE', 'https://example.com/images/watch.jpg', 0, NOW(), NOW()),
('无线键盘', 6, 799.00, 300, '机械键盘，无线连接，RGB背光', 'ON_SALE', 'https://example.com/images/keyboard.jpg', 0, NOW(), NOW()),
('游戏鼠标', 6, 399.00, 500, '电竞游戏鼠标，高精度传感器，RGB灯光', 'ON_SALE', 'https://example.com/images/mouse.jpg', 0, NOW(), NOW()),
('4K显示器 27英寸', 6, 3299.00, 60, '4K UHD显示器，27英寸，IPS面板，HDR支持', 'ON_SALE', 'https://example.com/images/monitor.jpg', 0, NOW(), NOW()),
('无线耳机', 3, 599.00, 400, '头戴式无线耳机，主动降噪，长续航', 'ON_SALE', 'https://example.com/images/headphone.jpg', 0, NOW(), NOW()),
('移动电源 20000mAh', 7, 199.00, 1000, '大容量移动电源，支持快充，Type-C接口', 'ON_SALE', 'https://example.com/images/powerbank.jpg', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- ============================================
-- 3. 初始化收货地址数据（可选）
-- ============================================

-- 为测试用户1添加默认地址
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `postal_code`, `is_default`, `created_at`, `updated_at`)
SELECT 
    2,  -- customer1@example.com的用户ID（假设是2）
    '张三',
    '13800000001',
    '北京市',
    '北京市',
    '朝阳区',
    '某某街道某某大厦1001室',
    '100000',
    1,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `user_address` WHERE `user_id` = 2 AND `is_default` = 1
);

-- ============================================
-- 说明
-- ============================================
-- 1. 用户密码说明：
--    - 所有测试账号的密码均为：password123
--    - 密码已使用BCrypt加密存储
--    - 管理员账号：admin@example.com / password123
--    - 如果密码无法登录，请通过注册接口重新注册或使用程序生成正确的BCrypt哈希值
-- 
-- 2. 商品数据说明：
--    - 共10个测试商品
--    - 所有商品默认状态为ON_SALE（上架）
--    - 图片URL为示例URL，实际使用时需要替换为真实图片地址
-- 
-- 3. 注意事项：
--    - 执行脚本前请确保已执行schema.sql建表脚本
--    - 如果用户已存在，会更新updated_at字段（ON DUPLICATE KEY UPDATE）
--    - 可以根据实际需要修改数据内容

