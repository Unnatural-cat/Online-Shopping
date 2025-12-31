-- ============================================
-- 修复重复分类数据的脚本
-- 执行此脚本可以清理数据库中的重复分类数据
-- ============================================

-- 方法1：删除重复的分类，只保留ID最小的那个
DELETE c1 FROM `category` c1
INNER JOIN `category` c2 
WHERE c1.id > c2.id AND c1.name = c2.name;

-- 方法2：如果方法1不适用，可以使用以下方法
-- 先查看重复的分类
-- SELECT name, COUNT(*) as count FROM `category` GROUP BY name HAVING count > 1;

-- 为name字段添加唯一索引（如果还没有）
-- ALTER TABLE `category` ADD UNIQUE KEY `uk_name` (`name`);

