# SQL脚本使用说明

## 文件说明

- `schema.sql` - 数据库建表脚本，包含所有表的DDL语句
- `data.sql` - 初始化数据脚本，包含测试用户和商品数据
- `indexes.sql` - 索引优化说明文档（不自动执行）

## 自动执行配置

项目已配置为在启动时自动执行SQL脚本，配置在 `application.properties` 中：

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:sql/schema.sql
spring.sql.init.data-locations=classpath:sql/data.sql
spring.sql.init.continue-on-error=true
```

## 使用场景

### 场景1：使用JPA自动创建表（开发环境推荐）

**配置**：
```properties
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.schema-locations=classpath:sql/schema.sql  # 可以注释掉，因为JPA会自动创建表
spring.sql.init.data-locations=classpath:sql/data.sql
```

**说明**：
- JPA会根据实体类自动创建/更新表结构
- 启动时只执行 `data.sql` 插入测试数据
- 适合开发环境，方便快速迭代

### 场景2：使用SQL脚本创建表（生产环境推荐）

**配置**：
```properties
spring.jpa.hibernate.ddl-auto=validate  # 或 none
spring.sql.init.schema-locations=classpath:sql/schema.sql
spring.sql.init.data-locations=classpath:sql/data.sql
```

**说明**：
- 使用SQL脚本创建表结构，更可控
- 先执行 `schema.sql` 创建表，再执行 `data.sql` 插入数据
- 适合生产环境，表结构由SQL脚本统一管理

## 执行顺序

Spring Boot会按以下顺序执行：

1. **JPA自动创建表**（如果 `ddl-auto=update`）
2. **执行 schema.sql**（如果配置了）
3. **执行 data.sql**

## 注意事项

1. **表已存在处理**：
   - SQL脚本中使用 `CREATE TABLE IF NOT EXISTS`，表已存在时不会报错
   - `spring.sql.init.continue-on-error=true` 确保即使出错也不会中断启动

2. **数据重复插入**：
   - `data.sql` 中使用 `ON DUPLICATE KEY UPDATE` 处理重复数据
   - 如果数据已存在，会更新 `updated_at` 字段

3. **密码哈希**：
   - 测试账号密码均为：`password123`
   - 密码已使用BCrypt加密存储
   - 如果密码无法登录，请通过注册接口重新注册

4. **生产环境**：
   - 建议将 `ddl-auto` 改为 `validate` 或 `none`
   - 使用SQL脚本统一管理表结构
   - 初始化数据脚本中的测试数据应替换为实际数据

## 手动执行（可选）

如果需要手动执行SQL脚本：

```bash
# 使用MySQL命令行
mysql -h <host> -u <username> -p <database> < schema.sql
mysql -h <host> -u <username> -p <database> < data.sql
```

## 验证执行结果

启动应用后，可以通过以下方式验证：

1. **查看启动日志**：查找 "Executing SQL script" 相关信息
2. **查询数据库**：
   ```sql
   SHOW TABLES;
   SELECT * FROM `user`;
   SELECT * FROM `product`;
   ```

## 测试账号

- **管理员**：admin@example.com / password123
- **测试用户1**：customer1@example.com / password123
- **测试用户2**：customer2@example.com / password123

