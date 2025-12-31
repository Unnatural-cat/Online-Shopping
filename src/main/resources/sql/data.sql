-- ============================================
-- 在线购物系统初始化数据脚本
-- 用于开发和测试环境
-- ============================================

-- 注意：密码使用BCrypt加密
-- 重要提示：
-- 1. 所有测试账号的密码均为：password123
-- 2. 应用启动时会自动检查并修复密码哈希值（通过DataInitializer）
-- 3. 如果密码哈希值不正确，系统会在启动时自动修复
-- 
-- 生成BCrypt哈希的方法：
-- 1. 通过注册接口注册新用户（推荐）
-- 2. 使用Java代码：BCryptPasswordEncoder.encode("password123")
-- 3. 使用在线BCrypt工具：https://bcrypt-generator.com/
-- 4. 运行工具类：com.example.OnlineShopping.util.PasswordHashGenerator

-- ============================================
-- 1. 初始化分类数据
-- ============================================
-- 注意：使用 INSERT ... SELECT ... WHERE NOT EXISTS 避免重复插入
-- 如果分类名称已存在，则跳过插入

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '手机', '智能手机、功能手机等', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '手机');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '笔记本电脑', '笔记本电脑、台式机等', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '笔记本电脑');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '耳机音响', '耳机、音响、音箱等音频设备', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '耳机音响');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '平板电脑', '平板电脑、电子阅读器等', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '平板电脑');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '智能穿戴', '智能手表、手环等可穿戴设备', 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '智能穿戴');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '电脑配件', '键盘、鼠标、显示器等电脑配件', 6, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '电脑配件');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '充电配件', '充电器、移动电源、数据线等', 7, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '充电配件');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '服装鞋帽', '服装、鞋子、帽子等', 8, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '服装鞋帽');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '家居用品', '家具、家电、生活用品等', 9, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '家居用品');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '食品饮料', '食品、饮料、零食等', 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '食品饮料');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '图书文具', '图书、文具、办公用品等', 11, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '图书文具');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '运动健身', '运动器材、健身用品等', 12, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '运动健身');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '美妆护肤', '化妆品、护肤品、个人护理等', 13, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '美妆护肤');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '汽车用品', '汽车配件、汽车装饰等', 14, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '汽车用品');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '母婴用品', '婴儿用品、孕妇用品等', 15, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '母婴用品');

INSERT INTO `category` (`name`, `description`, `sort_order`, `created_at`, `updated_at`)
SELECT '宠物用品', '宠物食品、宠物用品等', 16, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `category` WHERE `name` = '宠物用品');

-- ============================================
-- 2. 初始化用户数据
-- ============================================

-- 管理员账号（邮箱：admin@example.com，密码：password123）
-- 测试用户（密码：password123）
-- 注意：以下BCrypt哈希值为占位符，应用启动时会自动修复为正确的哈希值
-- 如果启动后仍无法登录，请检查DataInitializer是否正常运行
INSERT INTO `user` (`email`, `phone`, `password_hash`, `nickname`, `role`, `status`, `created_at`, `updated_at`)
VALUES 
('admin@example.com', '13800000000', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 'ADMIN', 'NORMAL', NOW(), NOW()),
('customer1@example.com', '13800000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户1', 'CUSTOMER', 'NORMAL', NOW(), NOW()),
('customer2@example.com', '13800000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户2', 'CUSTOMER', 'NORMAL', NOW(), NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- ============================================
-- 3. 初始化商品数据
-- ============================================
-- 注意：使用 INSERT ... SELECT ... WHERE NOT EXISTS 避免重复插入
-- 如果商品名称已存在，则跳过插入

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'iPhone 15 Pro Max 256GB', 1, 8999.00, 100, 'Apple iPhone 15 Pro Max，256GB存储，钛金属原色', 'ON_SALE', 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'iPhone 15 Pro Max 256GB');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'MacBook Pro 14英寸 M3', 2, 15999.00, 50, 'Apple MacBook Pro 14英寸，M3芯片，16GB内存，512GB SSD', 'ON_SALE', 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'MacBook Pro 14英寸 M3');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'AirPods Pro (第2代)', 3, 1899.00, 200, 'Apple AirPods Pro (第2代)，主动降噪，MagSafe充电盒', 'ON_SALE', 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'AirPods Pro (第2代)');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'iPad Air 11英寸', 4, 4799.00, 80, 'Apple iPad Air 11英寸，M2芯片，256GB存储', 'ON_SALE', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'iPad Air 11英寸');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Apple Watch Series 9', 5, 2999.00, 150, 'Apple Watch Series 9，45mm，GPS版本', 'ON_SALE', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Apple Watch Series 9');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '无线键盘', 6, 799.00, 300, '机械键盘，无线连接，RGB背光', 'ON_SALE', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '无线键盘');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '游戏鼠标', 6, 399.00, 500, '电竞游戏鼠标，高精度传感器，RGB灯光', 'ON_SALE', 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '游戏鼠标');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '4K显示器 27英寸', 6, 3299.00, 60, '4K UHD显示器，27英寸，IPS面板，HDR支持', 'ON_SALE', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '4K显示器 27英寸');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '无线耳机', 3, 599.00, 400, '头戴式无线耳机，主动降噪，长续航', 'ON_SALE', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '无线耳机');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '移动电源 20000mAh', 7, 199.00, 1000, '大容量移动电源，支持快充，Type-C接口', 'ON_SALE', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '移动电源 20000mAh');

-- ============================================
-- 继续添加更多商品（电子产品类）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '华为Mate 60 Pro 512GB', 1, 6999.00, 80, '华为Mate 60 Pro，512GB存储，麒麟9000S芯片，支持卫星通信', 'ON_SALE', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '华为Mate 60 Pro 512GB');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '小米14 Ultra 256GB', 1, 5999.00, 120, '小米14 Ultra，256GB存储，徕卡影像系统，120W快充', 'ON_SALE', 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '小米14 Ultra 256GB');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '联想ThinkPad X1 Carbon', 2, 12999.00, 40, '联想ThinkPad X1 Carbon，14英寸，i7处理器，16GB内存，512GB SSD', 'ON_SALE', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '联想ThinkPad X1 Carbon');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Sony WH-1000XM5 降噪耳机', 3, 2299.00, 150, 'Sony WH-1000XM5 头戴式降噪耳机，30小时续航，LDAC高音质', 'ON_SALE', 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Sony WH-1000XM5 降噪耳机');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '华为MatePad Pro 12.6英寸', 4, 4299.00, 90, '华为MatePad Pro 12.6英寸，麒麟9000E芯片，256GB存储，支持M-Pencil', 'ON_SALE', 'https://images.unsplash.com/photo-1561154464-82e9adf32750?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '华为MatePad Pro 12.6英寸');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '小米手环8 Pro', 5, 399.00, 500, '小米手环8 Pro，1.74英寸大屏，12天超长续航，150+运动模式', 'ON_SALE', 'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '小米手环8 Pro');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '罗技MX Master 3S 鼠标', 6, 699.00, 300, '罗技MX Master 3S 无线鼠标，精准追踪，多设备切换，人体工学设计', 'ON_SALE', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '罗技MX Master 3S 鼠标');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Cherry MX 3.0S 机械键盘', 6, 899.00, 200, 'Cherry MX 3.0S 机械键盘，Cherry原厂轴体，RGB背光，全键无冲', 'ON_SALE', 'https://images.unsplash.com/photo-1618384887929-16ec33cab9ef?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Cherry MX 3.0S 机械键盘');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'LG 27GP850 2K显示器', 6, 2499.00, 70, 'LG 27GP850 27英寸2K显示器，165Hz刷新率，Nano IPS面板，HDR400', 'ON_SALE', 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'LG 27GP850 2K显示器');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Anker 65W 氮化镓充电器', 7, 199.00, 800, 'Anker 65W 氮化镓充电器，多口快充，小巧便携，支持PD协议', 'ON_SALE', 'https://images.unsplash.com/photo-1587825147138-346b228a5b74?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Anker 65W 氮化镓充电器');

-- ============================================
-- 服装鞋帽类商品（category_id: 8）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Nike Air Max 270 运动鞋', 8, 899.00, 200, 'Nike Air Max 270 运动鞋，透气网面，气垫缓震，多色可选', 'ON_SALE', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Nike Air Max 270 运动鞋');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Adidas 三叶草经典T恤', 8, 299.00, 500, 'Adidas 三叶草经典T恤，100%纯棉，多色可选，舒适透气', 'ON_SALE', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Adidas 三叶草经典T恤');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '优衣库 摇粒绒连帽外套', 8, 199.00, 600, '优衣库 摇粒绒连帽外套，保暖舒适，多色可选，适合秋冬季节', 'ON_SALE', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '优衣库 摇粒绒连帽外套');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Levi\'s 501 经典牛仔裤', 8, 599.00, 300, 'Levi\'s 501 经典牛仔裤，直筒版型，100%纯棉，经典五袋设计', 'ON_SALE', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Levi\'s 501 经典牛仔裤');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'New Balance 574 复古跑鞋', 8, 599.00, 250, 'New Balance 574 复古跑鞋，经典设计，舒适缓震，多色可选', 'ON_SALE', 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'New Balance 574 复古跑鞋');

-- ============================================
-- 家居用品类商品（category_id: 9）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '小米米家空气净化器4', 9, 899.00, 150, '小米米家空气净化器4，高效过滤PM2.5，智能控制，静音运行', 'ON_SALE', 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '小米米家空气净化器4');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '飞利浦 智能台灯', 9, 299.00, 400, '飞利浦 智能台灯，护眼无频闪，多档调光，USB充电接口', 'ON_SALE', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '飞利浦 智能台灯');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '宜家 简约现代茶几', 9, 599.00, 100, '宜家 简约现代茶几，实木材质，北欧风格，适合小户型', 'ON_SALE', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '宜家 简约现代茶几');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '美的 电饭煲 4L', 9, 399.00, 300, '美的 电饭煲 4L，智能预约，24小时保温，不粘内胆', 'ON_SALE', 'https://images.unsplash.com/photo-1556910096-6f5e72db6803?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '美的 电饭煲 4L');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '无印良品 纯棉四件套', 9, 499.00, 200, '无印良品 纯棉四件套，1.5米床，简约设计，舒适亲肤', 'ON_SALE', 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '无印良品 纯棉四件套');

-- ============================================
-- 食品饮料类商品（category_id: 10）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '三只松鼠 坚果大礼包', 10, 128.00, 1000, '三只松鼠 坚果大礼包，1.5kg装，多种坚果组合，营养丰富', 'ON_SALE', 'https://images.unsplash.com/photo-1599599810769-92c0c5c9634a?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '三只松鼠 坚果大礼包');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '星巴克 咖啡豆 1kg', 10, 298.00, 500, '星巴克 咖啡豆 1kg，阿拉比卡豆，中度烘焙，浓郁香醇', 'ON_SALE', 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '星巴克 咖啡豆 1kg');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '好时 巧克力礼盒装', 10, 88.00, 800, '好时 巧克力礼盒装，500g，多种口味，精美包装，适合送礼', 'ON_SALE', 'https://images.unsplash.com/photo-1606312619070-d48b4afb4f2a?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '好时 巧克力礼盒装');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '农夫山泉 天然矿泉水 24瓶装', 10, 36.00, 2000, '农夫山泉 天然矿泉水 550ml*24瓶，天然水源，健康纯净', 'ON_SALE', 'https://images.unsplash.com/photo-1523362628745-0c100150b504?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '农夫山泉 天然矿泉水 24瓶装');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '康师傅 方便面 12桶装', 10, 45.00, 1500, '康师傅 方便面 12桶装，多种口味可选，方便快捷', 'ON_SALE', 'https://images.unsplash.com/photo-1612929633736-8d8a57b8b0e5?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '康师傅 方便面 12桶装');

-- ============================================
-- 图书文具类商品（category_id: 11）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '《三体》全集 精装版', 11, 128.00, 300, '《三体》全集 精装版，刘慈欣著，科幻小说经典，三册套装', 'ON_SALE', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '《三体》全集 精装版');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '派克 钢笔 经典款', 11, 299.00, 200, '派克 钢笔 经典款，不锈钢笔尖，流畅书写，精美礼盒装', 'ON_SALE', 'https://images.unsplash.com/photo-1583484963886-cfe2bff2945f?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '派克 钢笔 经典款');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '得力 办公文具套装', 11, 89.00, 600, '得力 办公文具套装，包含笔、尺、橡皮、订书机等，办公必备', 'ON_SALE', 'https://images.unsplash.com/photo-1484482340112-e1e2682b4856?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '得力 办公文具套装');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Moleskine 经典笔记本', 11, 199.00, 400, 'Moleskine 经典笔记本，硬皮封面，内页横线，192页，多色可选', 'ON_SALE', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Moleskine 经典笔记本');

-- ============================================
-- 运动健身类商品（category_id: 12）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'Keep 瑜伽垫 10mm', 12, 199.00, 300, 'Keep 瑜伽垫 10mm，TPE材质，防滑设计，适合瑜伽健身', 'ON_SALE', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'Keep 瑜伽垫 10mm');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '李宁 运动护腕套装', 12, 59.00, 500, '李宁 运动护腕套装，透气舒适，保护手腕，适合运动健身', 'ON_SALE', 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '李宁 运动护腕套装');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '迪卡侬 哑铃 5kg*2', 12, 199.00, 200, '迪卡侬 哑铃 5kg*2，包胶设计，防滑手柄，适合家庭健身', 'ON_SALE', 'https://images.unsplash.com/photo-1576678927484-cc907957088c?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '迪卡侬 哑铃 5kg*2');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '斯伯丁 篮球 标准7号', 12, 299.00, 150, '斯伯丁 篮球 标准7号，真皮材质，室内外通用，专业比赛用球', 'ON_SALE', 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '斯伯丁 篮球 标准7号');

-- ============================================
-- 美妆护肤类商品（category_id: 13）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '兰蔻 小黑瓶精华 50ml', 13, 1080.00, 100, '兰蔻 小黑瓶精华 50ml，修护肌底，改善肤质，适合所有肤质', 'ON_SALE', 'https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '兰蔻 小黑瓶精华 50ml');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '雅诗兰黛 小棕瓶眼霜 15ml', 13, 520.00, 150, '雅诗兰黛 小棕瓶眼霜 15ml，淡化细纹，紧致眼周，抗衰老', 'ON_SALE', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '雅诗兰黛 小棕瓶眼霜 15ml');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'SK-II 神仙水 230ml', 13, 1590.00, 80, 'SK-II 神仙水 230ml，Pitera精华，改善肌肤，适合油性肌肤', 'ON_SALE', 'https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'SK-II 神仙水 230ml');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT 'MAC 子弹头口红', 13, 180.00, 400, 'MAC 子弹头口红，经典色号，持久显色，多色可选', 'ON_SALE', 'https://images.unsplash.com/photo-1583241805024-65b5d8ae5b02?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = 'MAC 子弹头口红');

-- ============================================
-- 汽车用品类商品（category_id: 14）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '3M 汽车贴膜 全车套装', 14, 1299.00, 50, '3M 汽车贴膜 全车套装，防紫外线，隔热，隐私保护', 'ON_SALE', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '3M 汽车贴膜 全车套装');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '米其林 汽车轮胎 225/65R17', 14, 699.00, 100, '米其林 汽车轮胎 225/65R17，静音舒适，耐磨耐用，适合SUV', 'ON_SALE', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '米其林 汽车轮胎 225/65R17');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '360 行车记录仪', 14, 399.00, 200, '360 行车记录仪，1080P高清，夜视功能，停车监控', 'ON_SALE', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '360 行车记录仪');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '固特异 汽车脚垫 全包围', 14, 299.00, 300, '固特异 汽车脚垫 全包围，环保材质，防水防滑，易清洗', 'ON_SALE', 'https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '固特异 汽车脚垫 全包围');

-- ============================================
-- 母婴用品类商品（category_id: 15）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '帮宝适 纸尿裤 L码 80片', 15, 199.00, 500, '帮宝适 纸尿裤 L码 80片，超薄透气，防漏设计，适合6-11kg', 'ON_SALE', 'https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '帮宝适 纸尿裤 L码 80片');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '贝亲 奶瓶 240ml', 15, 89.00, 600, '贝亲 奶瓶 240ml，宽口径，PPSU材质，耐高温，易清洗', 'ON_SALE', 'https://images.unsplash.com/photo-1604881991720-f91add269bed?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '贝亲 奶瓶 240ml');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '费雪 婴儿摇椅', 15, 599.00, 100, '费雪 婴儿摇椅，电动摇摆，音乐安抚，适合0-18个月', 'ON_SALE', 'https://images.unsplash.com/photo-1503454537195-1dcabb73ffb9?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '费雪 婴儿摇椅');

-- ============================================
-- 宠物用品类商品（category_id: 16）
-- ============================================

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '皇家 成犬粮 15kg', 16, 599.00, 200, '皇家 成犬粮 15kg，营养均衡，适合成年犬，多种规格可选', 'ON_SALE', 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '皇家 成犬粮 15kg');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '猫砂盆 全封闭式', 16, 199.00, 300, '猫砂盆 全封闭式，防臭设计，大容量，适合多猫家庭', 'ON_SALE', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '猫砂盆 全封闭式');

INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `description`, `status`, `cover_image_url`, `sales_count`, `created_at`, `updated_at`)
SELECT '宠物牵引绳 可伸缩', 16, 89.00, 400, '宠物牵引绳 可伸缩，5米长度，适合中小型犬，安全可靠', 'ON_SALE', 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=800&h=600&fit=crop', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `product` WHERE `name` = '宠物牵引绳 可伸缩');

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
--    - 共50+个测试商品，涵盖多个类别
--    - 类别包括：电子产品、服装鞋帽、家居用品、食品饮料、图书文具、运动健身、美妆护肤、汽车用品、母婴用品、宠物用品
--    - 所有商品默认状态为ON_SALE（上架）
--    - 图片URL已使用Unsplash提供的免费高质量图片
--    - 图片尺寸：800x600，适合网页展示
--    - 使用 INSERT ... SELECT ... WHERE NOT EXISTS 避免重复插入
--    - 如果商品名称已存在，则跳过插入，不会重复添加
-- 
-- 3. 注意事项：
--    - 执行脚本前请确保已执行schema.sql建表脚本
--    - 如果用户已存在，会更新updated_at字段（ON DUPLICATE KEY UPDATE）
--    - 可以根据实际需要修改数据内容

