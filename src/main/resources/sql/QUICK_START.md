# SQL脚本自动执行 - 快速开始

## ✅ 配置已完成

项目已配置为在启动时自动执行SQL脚本，无需手动操作。

## 🚀 启动应用

直接启动Spring Boot应用即可：

```bash
# 使用Maven
mvn spring-boot:run

# 或使用IDE运行
# 运行 OnlineShoppingApplication.main()
```

## 📋 执行流程

启动时，Spring Boot会按以下顺序执行：

1. **连接数据库**（根据 `application.properties` 配置）
2. **JPA自动创建/更新表**（如果 `ddl-auto=update`）
3. **执行 schema.sql**（创建表结构，如果表已存在则跳过）
4. **执行 data.sql**（插入测试数据，如果数据已存在则更新）

## 🔍 验证执行结果

### 方法1：查看启动日志

启动应用后，在日志中查找以下信息：

```
Executing SQL script from URL [file:.../sql/schema.sql]
Executing SQL script from URL [file:.../sql/data.sql]
```

### 方法2：查询数据库

连接数据库后执行：

```sql
-- 查看所有表
SHOW TABLES;

-- 查看用户数据
SELECT id, email, nickname, role FROM `user`;

-- 查看商品数据
SELECT id, name, price, stock, status FROM `product`;
```

### 方法3：测试登录

使用测试账号登录验证：

- **管理员**：admin@example.com / password123
- **测试用户**：customer1@example.com / password123

## ⚙️ 配置说明

当前配置（`application.properties`）：

```properties
# JPA自动创建表（开发环境）
spring.jpa.hibernate.ddl-auto=update

# SQL脚本自动执行
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:sql/schema.sql
spring.sql.init.data-locations=classpath:sql/data.sql
spring.sql.init.continue-on-error=true
```

## 🔧 常见问题

### Q1: 表已存在，会报错吗？

**A**: 不会。SQL脚本使用 `CREATE TABLE IF NOT EXISTS`，表已存在时会自动跳过。

### Q2: 数据会重复插入吗？

**A**: 不会。`data.sql` 使用 `ON DUPLICATE KEY UPDATE`，如果数据已存在会更新而不是插入。

### Q3: 如何只执行数据脚本，不执行建表脚本？

**A**: 注释掉 `schema-locations` 配置：

```properties
# spring.sql.init.schema-locations=classpath:sql/schema.sql
spring.sql.init.data-locations=classpath:sql/data.sql
```

### Q4: 生产环境如何使用？

**A**: 修改配置：

```properties
# 使用SQL脚本创建表，不使用JPA自动创建
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.schema-locations=classpath:sql/schema.sql
spring.sql.init.data-locations=classpath:sql/data.sql
```

## 📝 注意事项

1. **首次启动**：会创建所有表和插入测试数据
2. **后续启动**：表已存在会跳过，数据已存在会更新
3. **密码**：测试账号密码均为 `password123`（BCrypt加密）
4. **生产环境**：请修改 `data.sql` 中的测试数据为实际数据

## 🎯 下一步

1. 启动应用验证SQL脚本是否正常执行
2. 使用测试账号登录验证数据是否正确
3. 根据需要调整配置（开发/生产环境）

