-- 为 order_item 表添加 product_image_url 字段
-- 用于保存订单创建时的商品图片URL快照，确保即使商品被删除也能显示图片

ALTER TABLE `order_item` 
ADD COLUMN `product_image_url` VARCHAR(500) NULL COMMENT '商品图片URL（快照）' 
AFTER `product_price`;

-- 为现有订单项更新商品图片URL（从商品表中获取）
UPDATE `order_item` oi
INNER JOIN `product` p ON oi.product_id = p.id
SET oi.product_image_url = p.cover_image_url
WHERE oi.product_image_url IS NULL AND p.cover_image_url IS NOT NULL;

