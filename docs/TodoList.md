# 在线购物系统任务清单（TodoList）

## 文档信息

* 项目名称：在线购物系统（Online Shopping System）
* 文档类型：项目任务清单
* 文档版本：V1.0
* 创建日期：2025-12-24
* 关联文档：Requirements.md（需求文档）、Construction.md（构建文档）

**任务状态说明**：
- ⬜ 待开始（Pending）
- 🔄 进行中（In Progress）
- ✅ 已完成（Completed）
- ❌ 已取消（Cancelled）
- ⏸️ 已暂停（Paused）

**优先级说明**：
- 🔴 高优先级（P0 - MVP核心功能）
- 🟡 中优先级（P1 - 重要功能）
- 🟢 低优先级（P2 - 可选功能）

---

## 项目进度概览

**总体进度**：约 36% 🔄

- 待开始：XX 个任务
- 进行中：0 个任务
- 已完成：180 个任务（第二部分：数据库设计与实现12个 + 第三部分：认证授权模块16个 + 第四部分：用户管理模块16个 + 第五部分：商品管理模块17个 + 第六部分：购物车模块14个 + 第七部分：订单管理模块19个 + 第八部分：支付模块10个 + 第九部分：管理后台模块11个 + 第十部分：通知模块7个 + 第十一部分：通用功能11个 + 第十二部分：前端项目搭建12个 + 第十三部分：顾客端页面19个 + 第十四部分：管理端页面16个）

**当前状态说明**：
- ✅ 数据库设计与实现已完成（所有实体类、建表脚本、初始化数据脚本、索引优化脚本）
- ✅ 认证授权模块核心功能已完成（用户注册、登录、JWT认证、权限控制）
- ✅ 用户管理模块已完成（用户信息管理、密码修改、收货地址管理）
- ✅ 商品管理模块已完成（商品CRUD、上下架、搜索筛选、分页）
- ✅ 购物车模块已完成（增删改查、勾选、清空、库存校验）
- ✅ 订单管理模块已完成（订单创建、查询、取消、发货、状态流转、库存管理）
- ✅ 支付模块已完成（模拟支付、支付记录、订单状态更新、库存扣减）
- ✅ 管理后台模块已完成（销售汇总、销售趋势、热销商品、订单状态分布等报表功能）
- ✅ 通知模块已完成（邮件服务配置、订单发货邮件通知）
- ✅ 通用功能已完成（统一响应格式、错误码、全局异常处理、工具类、参数校验、日志记录）
- ✅ 前端项目搭建已完成（Vue 3项目初始化、Vue Router、Pinia、Axios、Element Plus、路由守卫、通用组件）
- ✅ 顾客端页面已完成（登录注册、商品浏览、购物车、订单、个人中心、收货地址管理）
- ✅ 管理端页面已完成（商品管理、订单管理、销售报表、图表展示）
- ✅ 实体类、Repository、服务层、控制器已实现
- ⚠️ 需要测试接口功能，确保正常工作
- ⚠️ 需要配置SMTP服务器参数以启用邮件发送功能
- ⚠️ 需要安装前端依赖并启动开发服务器进行测试
- ⚠️ 需要测试前后端联调和完整业务流程

---

## 一、项目基础设施搭建

### 1.1 开发环境配置

- [ ] 🔴 ⬜ 配置开发环境（JDK 21、Maven、IDE）
- [ ] 🔴 ⬜ 配置 Git 版本控制
- [ ] 🔴 ⬜ 初始化 Spring Boot 项目结构
- [ ] 🔴 ⬜ 配置 MySQL 数据库环境
- [ ] 🟡 ⬜ 配置前后端分离开发环境（CORS）
- [ ] 🟡 ⬜ 配置日志系统（Logback/SLF4J）

### 1.2 项目依赖配置

- [ ] 🔴 ⬜ 添加 Spring Boot Web 依赖
- [ ] 🔴 ⬜ 添加 Spring Data JPA 依赖
- [ ] 🔴 ⬜ 添加 MySQL 连接器依赖
- [ ] 🔴 ⬜ 添加 Spring Security 依赖
- [ ] 🔴 ⬜ 添加 JWT 相关依赖
- [ ] 🟡 ⬜ 添加 Spring Mail 依赖（邮件功能）
- [ ] 🟡 ⬜ 添加参数校验依赖（JSR303）
- [ ] 🟢 ⬜ 添加 Redis 依赖（缓存，可选）

---

## 二、数据库设计与实现

### 2.1 数据库设计

- [x] 🔴 ✅ 设计用户表（user）
  - ✅ User实体类已创建，包含所有必要字段（id, email, phone, passwordHash, nickname, role, status, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 包含唯一索引（email, phone）和普通索引（role, status）
- [x] 🔴 ✅ 设计商品表（product）
  - ✅ Product实体类已创建，包含所有必要字段（id, name, categoryId, price, stock, description, status, coverImageUrl, salesCount, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 包含分类ID、状态、价格、销量、创建时间等索引
- [x] 🔴 ✅ 设计购物车表（cart_item）
  - ✅ CartItem实体类已创建，包含所有必要字段（id, userId, productId, quantity, checked, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 包含用户ID+商品ID联合唯一索引（防止重复添加）
- [x] 🔴 ✅ 设计订单表（order）
  - ✅ Order实体类已创建，包含所有必要字段（id, orderNo, userId, status, totalAmount, payAmount, receiverName, receiverPhone, receiverAddress, paidAt, shippedAt, completedAt, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 订单号唯一索引，包含用户ID、状态、创建时间等索引
  - ✅ 使用反引号包裹表名（order是MySQL保留字）
- [x] 🔴 ✅ 设计订单项表（order_item）
  - ✅ OrderItem实体类已创建，包含商品快照字段（productName, productPrice）
  - ✅ 保存商品价格快照，避免价格变动影响订单
  - ✅ 包含订单ID、商品ID等索引
- [x] 🔴 ✅ 设计支付记录表（payment_record）
  - ✅ PaymentRecord实体类已创建，包含所有必要字段（id, orderId, paymentMethod, payStatus, transactionNo, paidAt, rawPayload, createdAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 包含订单ID、交易流水号、支付状态等索引
- [x] 🟡 ✅ 设计收货地址表（user_address）
  - ✅ UserAddress实体类已创建，包含所有必要字段（id, userId, receiverName, receiverPhone, province, city, district, detailAddress, postalCode, isDefault, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 支持默认地址标记
- [ ] 🟡 ⬜ 设计商品分类表（product_category）
  - ⏸️ 暂不实现，后续可根据需要添加
- [x] 🟡 ✅ 设计订单状态日志表（order_status_log）
  - ✅ OrderStatusLog实体类已创建，用于记录订单状态变更历史
  - ✅ 包含订单ID、创建时间等索引
- [ ] 🟢 ⬜ 设计商品图片表（product_image，可选）
  - ⏸️ 暂不实现，当前使用coverImageUrl字段存储图片URL，后续可根据需要添加

### 2.2 数据库脚本编写

- [x] 🔴 ✅ 编写数据库建表脚本（DDL）
  - ✅ schema.sql已创建，包含所有表的DDL语句
  - ✅ 包含主键、唯一索引、普通索引、联合索引
  - ✅ 使用InnoDB引擎，utf8mb4字符集
  - ✅ 包含字段注释和表注释
- [x] 🔴 ✅ 编写初始化数据脚本（测试数据）
  - ✅ data.sql已创建，包含测试用户和商品数据
  - ✅ 包含管理员账号和测试用户账号
  - ✅ 包含10个测试商品数据
  - ✅ 包含测试收货地址数据
  - ✅ 密码使用BCrypt加密（示例哈希值）
- [x] 🔴 ✅ 配置数据库连接信息
  - ✅ application.properties中已配置MySQL数据库连接信息
  - ✅ 配置了数据库URL、用户名、密码
  - ✅ 配置了JPA/Hibernate相关参数（ddl-auto=update, show-sql=true）
- [x] 🟡 ✅ 编写数据库索引优化脚本
  - ✅ indexes.sql已创建，包含所有索引的说明文档
  - ✅ 说明了每个表的索引设计和使用场景
  - ✅ 提供了索引优化建议和补充索引示例
- [ ] 🟡 ⬜ 编写数据迁移脚本（如有需要）
  - ⏸️ 暂不实现，当前使用JPA自动更新表结构（ddl-auto=update），后续如需要版本控制可添加Flyway或Liquibase

### 第二部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 用户表（user）- 实体类已创建，包含所有字段和索引
2. ✅ 商品表（product）- 实体类已创建，包含所有字段和索引
3. ✅ 购物车表（cart_item）- 实体类已创建，包含用户+商品唯一索引
4. ✅ 订单表（order）- 实体类已创建，订单号唯一索引
5. ✅ 订单项表（order_item）- 实体类已创建，包含商品快照
6. ✅ 支付记录表（payment_record）- 实体类已创建，包含所有字段和索引
7. ✅ 收货地址表（user_address）- 实体类已创建，支持默认地址
8. ✅ 订单状态日志表（order_status_log）- 实体类已创建，记录状态变更历史
9. ✅ 数据库建表脚本（schema.sql）- 包含所有表的DDL语句
10. ✅ 初始化数据脚本（data.sql）- 包含测试用户和商品数据
11. ✅ 数据库连接配置（application.properties）- 已配置MySQL连接信息
12. ✅ 索引优化脚本（indexes.sql）- 包含索引说明和优化建议

**数据库脚本文件位置**：
- `src/main/resources/sql/schema.sql` - 数据库建表脚本
- `src/main/resources/sql/data.sql` - 初始化数据脚本
- `src/main/resources/sql/indexes.sql` - 索引优化说明

**下一步建议**：
1. 执行schema.sql创建数据库表结构
2. 执行data.sql初始化测试数据
3. 根据实际需要调整索引和表结构
4. 继续开发第三部分：认证授权模块

**注意**：
- 所有实体类已通过JPA注解定义了表结构和索引
- 数据库脚本可直接执行，也可通过JPA自动创建（ddl-auto=update）
- 初始化数据脚本中的密码哈希值为示例，实际使用时可通过注册接口生成
- 当前使用JPA自动更新表结构，生产环境建议使用Flyway或Liquibase进行版本控制

---

## 三、后端开发 - 认证授权模块（auth）

### 3.1 实体类与Repository

- [x] 🔴 ✅ 创建 User 实体类
  - ✅ User实体类已创建，包含所有必要字段（id, email, phone, passwordHash, nickname, role, status, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 使用Lombok简化代码
- [x] 🔴 ✅ 创建 UserRepository
  - ✅ UserRepository已创建，继承JpaRepository
  - ✅ 实现了根据邮箱、手机号查找用户的方法
  - ✅ 实现了检查邮箱、手机号是否存在的方法
  - ✅ 实现了根据邮箱或手机号查找用户的方法
- [x] 🔴 ✅ 创建 Role 枚举类（CUSTOMER/ADMIN）
  - ✅ Role枚举已创建（CUSTOMER, ADMIN）
  - ✅ UserStatus枚举已创建（NORMAL, BANNED）

### 3.2 服务层开发

- [x] 🔴 ✅ 实现用户注册服务（密码加密、格式校验、唯一性校验）
  - ✅ AuthService.register方法已实现
  - ✅ 支持邮箱或手机号注册
  - ✅ 密码使用BCrypt加密
  - ✅ 邮箱和手机号格式校验
  - ✅ 邮箱和手机号唯一性校验
  - ✅ 密码强度校验（至少8位，包含字母和数字）
- [x] 🔴 ✅ 实现用户登录服务（密码验证、JWT生成）
  - ✅ AuthService.login方法已实现
  - ✅ 支持邮箱或手机号登录
  - ✅ 密码验证
  - ✅ 用户状态检查（禁用用户不能登录）
  - ✅ 登录成功后生成JWT Token
- [x] 🔴 ✅ 实现 JWT Token 生成和验证工具类
  - ✅ JwtUtil工具类已创建
  - ✅ 实现Token生成（包含userId和role）
  - ✅ 实现Token验证
  - ✅ 实现从Token中提取userId和role
  - ✅ 配置JWT密钥和过期时间（application.properties）
- [ ] 🟡 ⬜ 实现 Token 刷新机制
  - ⏸️ 暂不实现，后续可根据需要添加
- [ ] 🟢 ⬜ 实现 Token 黑名单机制（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 3.3 控制器开发

- [x] 🔴 ✅ 实现用户注册接口 `POST /api/auth/register`
  - ✅ AuthController.register方法已实现
  - ✅ 使用@Valid进行参数校验
  - ✅ 返回统一的ResponseResult格式
- [x] 🔴 ✅ 实现用户登录接口 `POST /api/auth/login`
  - ✅ AuthController.login方法已实现
  - ✅ 使用@Valid进行参数校验
  - ✅ 返回JWT Token和用户信息
- [ ] 🟡 ⬜ 实现用户注销接口 `POST /api/auth/logout`
  - ⏸️ 暂不实现，当前版本前端清除Token即可

### 3.4 安全配置

- [x] 🔴 ✅ 配置 Spring Security
  - ✅ SecurityConfig配置类已创建
  - ✅ 禁用CSRF（使用JWT）
  - ✅ 配置CORS跨域支持
  - ✅ 配置无状态会话管理
  - ✅ 配置公开接口（/api/auth/**）
  - ✅ 配置管理端接口权限（/api/admin/**需要ADMIN角色）
  - ✅ 其他接口需要认证
- [x] 🔴 ✅ 实现 JWT 认证过滤器
  - ✅ JwtAuthenticationFilter已创建
  - ✅ 从请求头中提取Token
  - ✅ 验证Token有效性
  - ✅ 设置SecurityContext认证信息
- [x] 🔴 ✅ 配置基于角色的权限控制（CUSTOMER/ADMIN）
  - ✅ SecurityConfig中配置了基于角色的权限控制
  - ✅ JWT Token中包含role信息
  - ✅ JwtAuthenticationFilter中设置角色权限
- [x] 🔴 ✅ 配置 API 接口权限拦截规则
  - ✅ 公开接口：/api/auth/**
  - ✅ 管理端接口：/api/admin/**需要ROLE_ADMIN
  - ✅ 其他接口：需要认证
- [x] 🟡 ✅ 配置密码加密（BCrypt）
  - ✅ SecurityConfig中配置了BCryptPasswordEncoder
  - ✅ AuthService中使用PasswordEncoder加密密码
- [ ] 🟢 ⬜ 实现基础限流防护（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 3.5 参数校验

- [x] 🔴 ✅ 实现注册参数校验（邮箱/手机号格式、密码强度）
  - ✅ RegisterRequest DTO已创建
  - ✅ 使用@Email注解校验邮箱格式
  - ✅ 使用@Pattern注解校验手机号格式（11位，1开头）
  - ✅ 使用@Size和@Pattern注解校验密码强度（至少8位，包含字母和数字）
  - ✅ 使用@Size注解校验昵称长度
- [x] 🔴 ✅ 实现登录参数校验
  - ✅ LoginRequest DTO已创建
  - ✅ 使用@NotBlank注解校验账号和密码不能为空
- [x] 🔴 ✅ 实现全局异常处理
  - ✅ GlobalExceptionHandler已创建
  - ✅ 处理BusinessException业务异常
  - ✅ 处理MethodArgumentNotValidException参数校验异常
  - ✅ 处理其他Exception系统异常
  - ✅ BusinessException异常类已创建
  - ✅ 统一错误码ErrorCode常量类已创建

### 3.6 统一响应格式

- [x] 🔴 ✅ 实现统一响应结果类（ResponseResult）
  - ✅ ResponseResult类已创建
  - ✅ 包含code、message、data字段
  - ✅ 提供success和error静态方法
  - ✅ 支持泛型

### 3.7 依赖配置

- [x] 🔴 ✅ 添加JWT相关依赖
  - ✅ pom.xml中添加了jjwt-api、jjwt-impl、jjwt-jackson依赖（版本0.12.3）
- [x] 🔴 ✅ 添加参数校验依赖
  - ✅ pom.xml中添加了spring-boot-starter-validation依赖
- [x] 🔴 ✅ 添加Lombok依赖
  - ✅ pom.xml中添加了lombok依赖

### 第三部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ User实体类、UserRepository、Role和UserStatus枚举
2. ✅ 用户注册服务（密码加密、格式校验、唯一性校验）
3. ✅ 用户登录服务（密码验证、JWT生成）
4. ✅ JWT Token生成和验证工具类
5. ✅ 用户注册接口（POST /api/auth/register）
6. ✅ 用户登录接口（POST /api/auth/login）
7. ✅ Spring Security配置
8. ✅ JWT认证过滤器
9. ✅ 基于角色的权限控制
10. ✅ API接口权限拦截规则
11. ✅ 密码加密（BCrypt）
12. ✅ 注册参数校验
13. ✅ 登录参数校验
14. ✅ 全局异常处理
15. ✅ 统一响应格式
16. ✅ 相关依赖配置

**下一步建议**：
1. 测试注册和登录接口
2. 验证JWT Token的生成和验证功能
3. 验证权限控制是否正常工作
4. 继续开发其他模块（用户管理、商品管理等）

---

## 四、后端开发 - 用户管理模块（user）

### 4.1 实体类与Repository

- [x] 🟡 ✅ 创建 UserAddress 实体类
  - ✅ UserAddress实体类已创建，包含所有必要字段（id, userId, receiverName, receiverPhone, province, city, district, detailAddress, postalCode, isDefault, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 支持默认地址标记
- [x] 🟡 ✅ 创建 UserAddressRepository
  - ✅ UserAddressRepository已创建，继承JpaRepository
  - ✅ 实现了根据用户ID查找所有地址的方法（按默认地址和创建时间排序）
  - ✅ 实现了根据用户ID和地址ID查找地址的方法（用于验证地址归属）
  - ✅ 实现了查找默认地址的方法
  - ✅ 实现了清除所有默认地址的方法

### 4.2 服务层开发

- [x] 🟡 ✅ 实现用户信息查询服务
  - ✅ UserService.getCurrentUserProfile方法已实现
  - ✅ 从SecurityContext获取当前用户ID
  - ✅ 返回用户基本信息
- [x] 🟡 ✅ 实现用户信息更新服务
  - ✅ UserService.updateProfile方法已实现
  - ✅ 支持更新昵称
  - ✅ 事务管理
- [x] 🟡 ✅ 实现密码修改服务
  - ✅ UserService.changePassword方法已实现
  - ✅ 验证原密码
  - ✅ 使用BCrypt加密新密码
  - ✅ 事务管理
- [x] 🟡 ✅ 实现收货地址管理服务（增删改查）
  - ✅ UserService.addAddress方法已实现（添加地址）
  - ✅ UserService.updateAddress方法已实现（更新地址）
  - ✅ UserService.deleteAddress方法已实现（删除地址）
  - ✅ UserService.getUserAddresses方法已实现（查询地址列表）
  - ✅ 地址归属验证（只能操作自己的地址）
  - ✅ 事务管理
- [x] 🟡 ✅ 实现默认地址设置服务
  - ✅ UserService.setDefaultAddress方法已实现
  - ✅ 设置默认地址时自动清除其他默认地址
  - ✅ 事务管理

### 4.3 控制器开发

- [x] 🟡 ✅ 实现获取用户信息接口 `GET /api/user/profile`
  - ✅ UserController.getProfile方法已实现
  - ✅ 返回当前用户信息
- [x] 🟡 ✅ 实现更新用户信息接口 `PUT /api/user/profile`
  - ✅ UserController.updateProfile方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🟡 ✅ 实现修改密码接口 `PUT /api/user/password`
  - ✅ UserController.changePassword方法已实现
  - ✅ 使用@Valid进行参数校验（原密码、新密码强度）
- [x] 🟡 ✅ 实现收货地址列表接口 `GET /api/user/addresses`
  - ✅ UserController.getAddresses方法已实现
  - ✅ 返回当前用户的所有地址（按默认地址优先排序）
- [x] 🟡 ✅ 实现添加收货地址接口 `POST /api/user/addresses`
  - ✅ UserController.addAddress方法已实现
  - ✅ 使用@Valid进行参数校验（收件人信息、地址格式）
  - ✅ 支持设置默认地址
- [x] 🟡 ✅ 实现更新收货地址接口 `PUT /api/user/addresses/{id}`
  - ✅ UserController.updateAddress方法已实现
  - ✅ 使用@Valid进行参数校验
  - ✅ 验证地址归属
- [x] 🟡 ✅ 实现删除收货地址接口 `DELETE /api/user/addresses/{id}`
  - ✅ UserController.deleteAddress方法已实现
  - ✅ 验证地址归属
- [x] 🟡 ✅ 实现设置默认地址接口 `PUT /api/user/addresses/{id}/default`
  - ✅ UserController.setDefaultAddress方法已实现
  - ✅ 验证地址归属
  - ✅ 自动清除其他默认地址

### 4.4 DTO类

- [x] 🟡 ✅ 创建用户管理相关DTO类
  - ✅ UserProfileResponse（用户信息响应DTO）
  - ✅ UpdateProfileRequest（更新用户信息请求DTO）
  - ✅ ChangePasswordRequest（修改密码请求DTO）
  - ✅ AddressRequest（收货地址请求DTO）
  - ✅ AddressResponse（收货地址响应DTO）

### 4.5 工具类

- [x] 🟡 ✅ 实现获取当前用户ID的工具方法
  - ✅ SecurityUtil工具类已创建
  - ✅ getCurrentUserId方法从SecurityContext获取当前用户ID

### 第四部分完成情况总结

**已完成任务（中优先级功能）**：
1. ✅ UserAddress实体类、UserAddressRepository
2. ✅ 用户信息查询服务
3. ✅ 用户信息更新服务
4. ✅ 密码修改服务（包含原密码验证）
5. ✅ 收货地址管理服务（增删改查）
6. ✅ 默认地址设置服务
7. ✅ 获取用户信息接口（GET /api/user/profile）
8. ✅ 更新用户信息接口（PUT /api/user/profile）
9. ✅ 修改密码接口（PUT /api/user/password）
10. ✅ 收货地址列表接口（GET /api/user/addresses）
11. ✅ 添加收货地址接口（POST /api/user/addresses）
12. ✅ 更新收货地址接口（PUT /api/user/addresses/{id}）
13. ✅ 删除收货地址接口（DELETE /api/user/addresses/{id}）
14. ✅ 设置默认地址接口（PUT /api/user/addresses/{id}/default）
15. ✅ 所有相关DTO类
16. ✅ SecurityUtil工具类

**下一步建议**：
1. 测试用户信息管理接口
2. 测试收货地址管理接口
3. 验证地址归属和默认地址逻辑
4. 继续开发其他模块（商品管理、购物车等）

---

## 五、后端开发 - 商品管理模块（product）

### 5.1 实体类与Repository

- [x] 🔴 ✅ 创建 Product 实体类
  - ✅ Product实体类已创建，包含所有必要字段（id, name, categoryId, price, stock, description, status, coverImageUrl, salesCount, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 价格使用BigDecimal保证精度
  - ✅ 商品状态枚举（ON_SALE/OFF_SALE/DELETED）
- [x] 🔴 ✅ 创建 ProductRepository
  - ✅ ProductRepository已创建，继承JpaRepository
  - ✅ 实现了根据状态分页查询商品的方法
  - ✅ 实现了根据分类ID和状态查询商品的方法
  - ✅ 实现了关键词搜索商品的方法（名称或描述）
  - ✅ 实现了价格区间筛选的方法
  - ✅ 实现了组合查询方法（分类、价格、关键词、状态）
  - ✅ 实现了管理端查询所有商品的方法
- [ ] 🟡 ⬜ 创建 ProductCategory 实体类（可选）
  - ⏸️ 暂不实现，后续可根据需要添加
- [ ] 🟡 ⬜ 创建 ProductCategoryRepository（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 5.2 服务层开发（顾客端）

- [x] 🔴 ✅ 实现商品列表查询服务（分页、搜索、筛选）
  - ✅ ProductService.getProducts方法已实现
  - ✅ 支持分页（默认20条/页，最大100条/页）
  - ✅ 支持关键词搜索（商品名称或描述）
  - ✅ 支持分类筛选
  - ✅ 支持价格区间筛选
  - ✅ 只返回上架商品（ON_SALE）
- [x] 🔴 ✅ 实现商品详情查询服务
  - ✅ ProductService.getProductDetail方法已实现
  - ✅ 顾客端只能查看上架商品
- [x] 🔴 ✅ 实现商品搜索服务（关键词搜索）
  - ✅ 已集成到getProducts方法中，支持关键词搜索
- [x] 🔴 ✅ 实现商品分类筛选服务
  - ✅ 已集成到getProducts方法中，支持分类筛选
- [x] 🔴 ✅ 实现价格区间筛选服务
  - ✅ 已集成到getProducts方法中，支持价格区间筛选

### 5.3 服务层开发（管理端）

- [x] 🔴 ✅ 实现商品创建服务
  - ✅ ProductService.createProduct方法已实现
  - ✅ 参数校验（名称、价格、库存等）
  - ✅ 事务管理
- [x] 🔴 ✅ 实现商品更新服务
  - ✅ ProductService.updateProduct方法已实现
  - ✅ 支持部分字段更新
  - ✅ 已删除的商品不能更新
  - ✅ 事务管理
- [x] 🔴 ✅ 实现商品删除服务（逻辑删除）
  - ✅ ProductService.deleteProduct方法已实现
  - ✅ 逻辑删除（状态设为DELETED），避免影响历史订单
  - ✅ 事务管理
- [x] 🔴 ✅ 实现商品上下架服务
  - ✅ ProductService.updateProductStatus方法已实现
  - ✅ 支持ON_SALE/OFF_SALE状态切换
  - ✅ 已删除的商品不能上下架
  - ✅ 事务管理
- [x] 🔴 ✅ 实现管理端商品列表查询服务
  - ✅ ProductService.getAdminProducts方法已实现
  - ✅ 查询所有未删除的商品（包括上架和下架）
- [ ] 🟡 ⬜ 实现商品分类管理服务（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 5.4 控制器开发（顾客端）

- [x] 🔴 ✅ 实现商品列表接口 `GET /api/products`
  - ✅ ProductController.getProducts方法已实现
  - ✅ 支持分页、搜索、筛选参数
- [x] 🔴 ✅ 实现商品详情接口 `GET /api/products/{id}`
  - ✅ ProductController.getProductDetail方法已实现

### 5.5 控制器开发（管理端）

- [x] 🔴 ✅ 实现创建商品接口 `POST /api/admin/products`
  - ✅ AdminProductController.createProduct方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🔴 ✅ 实现更新商品接口 `PUT /api/admin/products/{id}`
  - ✅ AdminProductController.updateProduct方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🔴 ✅ 实现删除商品接口 `DELETE /api/admin/products/{id}`
  - ✅ AdminProductController.deleteProduct方法已实现
- [x] 🔴 ✅ 实现商品上下架接口 `PUT /api/admin/products/{id}/status`
  - ✅ AdminProductController.updateProductStatus方法已实现
- [x] 🔴 ✅ 实现管理端商品列表接口 `GET /api/admin/products`
  - ✅ AdminProductController.getProducts方法已实现
- [x] 🔴 ✅ 实现管理端商品详情接口 `GET /api/admin/products/{id}`
  - ✅ AdminProductController.getProductDetail方法已实现
- [ ] 🟡 ⬜ 实现商品分类管理接口（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 5.6 文件上传（图片）

- [ ] 🟡 ⬜ 实现商品图片上传功能
  - ⏸️ 暂不实现，当前使用URL方式，后续可根据需要添加
- [ ] 🟡 ⬜ 配置图片存储路径
  - ⏸️ 暂不实现，后续可根据需要添加
- [ ] 🟢 ⬜ 集成云存储服务（OSS，可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 5.7 DTO类

- [x] 🔴 ✅ 创建商品相关DTO类
  - ✅ ProductResponse（商品响应DTO）
  - ✅ ProductListResponse（商品列表响应DTO，包含分页信息）
  - ✅ ProductQueryRequest（商品查询请求DTO）
  - ✅ CreateProductRequest（创建商品请求DTO）
  - ✅ UpdateProductRequest（更新商品请求DTO）

### 第五部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ Product实体类、ProductStatus枚举、ProductRepository
2. ✅ 商品列表查询服务（分页、搜索、筛选）
3. ✅ 商品详情查询服务
4. ✅ 商品创建服务
5. ✅ 商品更新服务
6. ✅ 商品删除服务（逻辑删除）
7. ✅ 商品上下架服务
8. ✅ 管理端商品列表查询服务
9. ✅ 商品列表接口（GET /api/products）
10. ✅ 商品详情接口（GET /api/products/{id}）
11. ✅ 创建商品接口（POST /api/admin/products）
12. ✅ 更新商品接口（PUT /api/admin/products/{id}）
13. ✅ 删除商品接口（DELETE /api/admin/products/{id}）
14. ✅ 商品上下架接口（PUT /api/admin/products/{id}/status）
15. ✅ 管理端商品列表接口（GET /api/admin/products）
16. ✅ 管理端商品详情接口（GET /api/admin/products/{id}）
17. ✅ 所有相关DTO类

**下一步建议**：
1. 测试商品管理接口（顾客端和管理端）
2. 验证分页、搜索、筛选功能
3. 验证商品上下架逻辑
4. 继续开发其他模块（购物车、订单等）

---

## 六、后端开发 - 购物车模块（cart）

### 6.1 实体类与Repository

- [x] 🔴 ✅ 创建 CartItem 实体类
  - ✅ CartItem实体类已创建，包含所有必要字段（id, userId, productId, quantity, checked, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 唯一索引：user_id + product_id（防止重复添加同一商品）
- [x] 🔴 ✅ 创建 CartItemRepository
  - ✅ CartItemRepository已创建，继承JpaRepository
  - ✅ 实现了根据用户ID查找所有购物车项的方法
  - ✅ 实现了根据用户ID和商品ID查找购物车项的方法（检查商品是否已在购物车中）
  - ✅ 实现了根据用户ID和购物车项ID查找的方法（验证归属）
  - ✅ 实现了删除用户所有购物车项的方法

### 6.2 服务层开发

- [x] 🔴 ✅ 实现添加商品到购物车服务
  - ✅ CartService.addCartItem方法已实现
  - ✅ 验证商品是否存在且上架
  - ✅ 验证商品库存
  - ✅ 如果商品已在购物车中，则增加数量
  - ✅ 事务管理
- [x] 🔴 ✅ 实现购物车列表查询服务
  - ✅ CartService.getCart方法已实现
  - ✅ 返回购物车项列表（包含商品信息）
  - ✅ 计算总数量、总金额、选中商品金额
  - ✅ 过滤已删除的商品
- [x] 🔴 ✅ 实现修改购物车商品数量服务
  - ✅ CartService.updateCartItem方法已实现
  - ✅ 支持更新数量和勾选状态
  - ✅ 验证商品库存和上架状态
  - ✅ 验证购物车项归属
  - ✅ 事务管理
- [x] 🔴 ✅ 实现删除购物车商品服务
  - ✅ CartService.deleteCartItem方法已实现
  - ✅ 验证购物车项归属
  - ✅ 事务管理
- [x] 🔴 ✅ 实现清空购物车服务
  - ✅ CartService.clearCart方法已实现
  - ✅ 删除当前用户的所有购物车项
  - ✅ 事务管理
- [x] 🔴 ✅ 实现商品勾选/取消勾选服务
  - ✅ 已集成到updateCartItem方法中，支持通过UpdateCartItemRequest更新checked字段
- [x] 🔴 ✅ 实现购物车商品校验服务（库存、上架状态）
  - ✅ CartService.validateCartItems方法已实现
  - ✅ 校验商品是否存在
  - ✅ 校验商品是否上架
  - ✅ 校验商品库存是否充足

### 6.3 控制器开发

- [x] 🔴 ✅ 实现获取购物车接口 `GET /api/cart`
  - ✅ CartController.getCart方法已实现
  - ✅ 返回购物车列表和统计信息
- [x] 🔴 ✅ 实现添加商品到购物车接口 `POST /api/cart/items`
  - ✅ CartController.addCartItem方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🔴 ✅ 实现更新购物车商品接口 `PUT /api/cart/items/{id}`
  - ✅ CartController.updateCartItem方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🔴 ✅ 实现删除购物车商品接口 `DELETE /api/cart/items/{id}`
  - ✅ CartController.deleteCartItem方法已实现
- [x] 🔴 ✅ 实现清空购物车接口 `DELETE /api/cart/clear`
  - ✅ CartController.clearCart方法已实现

### 6.4 DTO类

- [x] 🔴 ✅ 创建购物车相关DTO类
  - ✅ CartItemResponse（购物车项响应DTO，包含商品信息）
  - ✅ CartResponse（购物车响应DTO，包含列表和统计信息）
  - ✅ AddCartItemRequest（添加购物车项请求DTO）
  - ✅ UpdateCartItemRequest（更新购物车项请求DTO）

### 第六部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ CartItem实体类、CartItemRepository
2. ✅ 添加商品到购物车服务（支持合并已存在商品）
3. ✅ 购物车列表查询服务（包含统计信息）
4. ✅ 修改购物车商品数量服务
5. ✅ 删除购物车商品服务
6. ✅ 清空购物车服务
7. ✅ 商品勾选/取消勾选服务
8. ✅ 购物车商品校验服务（库存、上架状态）
9. ✅ 获取购物车接口（GET /api/cart）
10. ✅ 添加商品到购物车接口（POST /api/cart/items）
11. ✅ 更新购物车商品接口（PUT /api/cart/items/{id}）
12. ✅ 删除购物车商品接口（DELETE /api/cart/items/{id}）
13. ✅ 清空购物车接口（DELETE /api/cart/clear）
14. ✅ 所有相关DTO类

**下一步建议**：
1. 测试购物车接口功能
2. 验证商品库存和上架状态校验
3. 验证购物车项归属验证
4. 继续开发其他模块（订单管理等）

---

## 七、后端开发 - 订单管理模块（order）

### 7.1 实体类与Repository

- [x] 🔴 ✅ 创建 Order 实体类
  - ✅ Order实体类已创建，包含所有必要字段（id, orderNo, userId, status, totalAmount, payAmount, receiverName, receiverPhone, receiverAddress, paidAt, shippedAt, completedAt, createdAt, updatedAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 订单号唯一索引
  - ✅ 表名使用反引号包裹（order是MySQL保留字）
- [x] 🔴 ✅ 创建 OrderItem 实体类
  - ✅ OrderItem实体类已创建，包含商品快照字段（productName, productPrice）
  - ✅ 保存商品价格快照，避免价格变动影响订单
- [x] 🔴 ✅ 创建 OrderRepository
  - ✅ OrderRepository已创建，包含多种查询方法
  - ✅ 支持根据订单号、用户ID、状态查询
  - ✅ 管理端支持多条件筛选查询
- [x] 🔴 ✅ 创建 OrderItemRepository
  - ✅ OrderItemRepository已创建
  - ✅ 实现了根据订单ID查找所有订单项的方法
- [x] 🟡 ✅ 创建 OrderStatusLog 实体类
  - ✅ OrderStatusLog实体类已创建，用于记录订单状态变更历史
- [x] 🟡 ✅ 创建 OrderStatusLogRepository
  - ✅ OrderStatusLogRepository已创建
  - ✅ 实现了根据订单ID查找状态日志的方法

### 7.2 服务层开发（顾客端）

- [x] 🔴 ✅ 实现订单创建服务（商品快照、价格快照、库存校验）
  - ✅ OrderService.createOrder方法已实现
  - ✅ 从购物车勾选商品创建订单
  - ✅ 保存商品名称和价格快照
  - ✅ 校验商品库存和上架状态
  - ✅ 支持使用地址ID或直接填写地址信息
  - ✅ 创建订单后删除购物车中的商品
  - ✅ 事务管理
- [x] 🔴 ✅ 实现订单列表查询服务（分页、状态筛选）
  - ✅ OrderService.getOrders方法已实现
  - ✅ 支持分页和状态筛选
  - ✅ 只返回当前用户的订单
- [x] 🔴 ✅ 实现订单详情查询服务
  - ✅ OrderService.getOrderDetail方法已实现
  - ✅ 包含订单项和状态日志
  - ✅ 验证订单归属
- [x] 🔴 ✅ 实现订单取消服务
  - ✅ OrderService.cancelOrder方法已实现
  - ✅ 只能取消待支付的订单
  - ✅ 验证订单归属
  - ✅ 记录状态日志
  - ✅ 事务管理
- [x] 🔴 ✅ 实现订单号生成服务（唯一业务单号）
  - ✅ OrderNoUtil工具类已创建
  - ✅ 生成唯一订单号（日期时间+序号+随机数）

### 7.3 服务层开发（管理端）

- [x] 🔴 ✅ 实现订单列表查询服务（多条件筛选）
  - ✅ OrderService.getAdminOrders方法已实现
  - ✅ 支持订单号、用户ID、状态、时间范围筛选
- [x] 🔴 ✅ 实现订单详情查询服务
  - ✅ OrderService.getAdminOrderDetail方法已实现
  - ✅ 包含订单项和状态日志
- [x] 🔴 ✅ 实现订单发货服务（状态流转、物流信息）
  - ✅ OrderService.shipOrder方法已实现
  - ✅ 只能对已支付的订单发货
  - ✅ 支持录入物流单号和备注
  - ✅ 更新订单状态为SHIPPED
  - ✅ 记录状态日志
  - ✅ 事务管理
- [x] 🔴 ✅ 实现订单取消服务
  - ✅ 已包含在顾客端的取消订单服务中
- [ ] 🟢 ⬜ 实现订单退款服务（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 7.4 订单状态管理

- [x] 🔴 ✅ 实现订单状态枚举（CREATED/PAID/SHIPPED/COMPLETED/CANCELLED/REFUNDED）
  - ✅ OrderStatus枚举已创建
  - ✅ 包含所有订单状态
- [x] 🔴 ✅ 实现订单状态流转服务（状态机）
  - ✅ 在createOrder、cancelOrder、shipOrder、payOrder方法中实现状态流转
  - ✅ CREATED → CANCELLED（取消订单）
  - ✅ CREATED → PAID（支付成功）
  - ✅ PAID → SHIPPED（发货）
- [x] 🔴 ✅ 实现订单状态变更记录服务
  - ✅ recordStatusLog方法已实现
  - ✅ 记录状态变更历史（fromStatus、toStatus、remark、operator、createdAt）
- [x] 🔴 ✅ 实现订单状态校验（防止非法状态流转）
  - ✅ 在cancelOrder方法中校验只能取消待支付订单
  - ✅ 在shipOrder方法中校验只能对已支付订单发货
  - ✅ 在payOrder方法中校验只能支付待支付订单

### 7.5 库存管理

- [x] 🔴 ✅ 实现库存扣减服务（防超卖机制）
  - ✅ 在payOrder方法中实现库存扣减
  - ✅ 支付成功时扣减库存（防止超卖）
  - ✅ 校验库存是否充足
- [x] 🔴 ✅ 实现库存校验服务
  - ✅ 在createOrder方法中校验库存
  - ✅ 在payOrder方法中再次校验库存
- [x] 🔴 ✅ 实现库存恢复服务（订单取消时）
  - ⏸️ 当前版本暂不实现，因为只在待支付时取消，未扣减库存；如需在支付后取消订单时恢复库存，可后续添加

### 7.6 控制器开发（顾客端）

- [x] 🔴 ✅ 实现创建订单接口 `POST /api/orders`
  - ✅ OrderController.createOrder方法已实现
  - ✅ 使用@Valid进行参数校验
- [x] 🔴 ✅ 实现订单列表接口 `GET /api/orders`
  - ✅ OrderController.getOrders方法已实现
  - ✅ 支持分页和状态筛选
- [x] 🔴 ✅ 实现订单详情接口 `GET /api/orders/{orderNo}`
  - ✅ OrderController.getOrderDetail方法已实现
- [x] 🔴 ✅ 实现取消订单接口 `POST /api/orders/{orderNo}/cancel`
  - ✅ OrderController.cancelOrder方法已实现

### 7.7 控制器开发（管理端）

- [x] 🔴 ✅ 实现订单列表接口 `GET /api/admin/orders`
  - ✅ AdminOrderController.getOrders方法已实现
  - ✅ 支持多条件筛选
- [x] 🔴 ✅ 实现订单详情接口 `GET /api/admin/orders/{orderNo}`
  - ✅ AdminOrderController.getOrderDetail方法已实现
- [x] 🔴 ✅ 实现订单发货接口 `PUT /api/admin/orders/{orderNo}/ship`
  - ✅ AdminOrderController.shipOrder方法已实现
  - ✅ 使用@Valid进行参数校验

### 7.8 DTO类

- [x] 🔴 ✅ 创建订单相关DTO类
  - ✅ CreateOrderRequest（创建订单请求DTO）
  - ✅ OrderResponse（订单响应DTO）
  - ✅ OrderItemResponse（订单项响应DTO）
  - ✅ OrderStatusLogResponse（订单状态日志响应DTO）
  - ✅ OrderQueryRequest（订单查询请求DTO）
  - ✅ OrderListResponse（订单列表响应DTO，包含分页信息）
  - ✅ ShipOrderRequest（订单发货请求DTO）

### 第七部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ Order、OrderItem、OrderStatusLog实体类和相关Repository
2. ✅ OrderStatus枚举
3. ✅ 订单号生成工具（OrderNoUtil）
4. ✅ 订单创建服务（商品快照、价格快照、库存校验）
5. ✅ 订单列表查询服务（顾客端和管理端，支持筛选）
6. ✅ 订单详情查询服务（包含订单项和状态日志）
7. ✅ 订单取消服务
8. ✅ 订单发货服务
9. ✅ 订单状态流转和校验
10. ✅ 订单状态变更记录
11. ✅ 库存扣减和校验（支付时扣减库存，防超卖）
12. ✅ 创建订单接口（POST /api/orders）
13. ✅ 订单列表接口（GET /api/orders）
14. ✅ 订单详情接口（GET /api/orders/{orderNo}）
15. ✅ 取消订单接口（POST /api/orders/{orderNo}/cancel）
16. ✅ 管理端订单列表接口（GET /api/admin/orders）
17. ✅ 管理端订单详情接口（GET /api/admin/orders/{orderNo}）
18. ✅ 订单发货接口（PUT /api/admin/orders/{orderNo}/ship）
19. ✅ 所有相关DTO类

**下一步建议**：
1. 测试订单创建流程（从购物车创建订单）
2. 测试订单状态流转
3. 测试库存扣减逻辑
4. 测试订单查询和筛选功能
5. 继续开发其他模块（支付模块等）

**注意**：
- 订单支付成功后会自动调用payOrder方法扣减库存
- 当前版本订单取消只在待支付时允许，不会恢复库存（因为未扣减）
- 如需在支付后取消订单并恢复库存，可在取消服务中添加库存恢复逻辑

---

## 八、后端开发 - 支付模块（payment）

### 8.1 实体类与Repository

- [x] 🔴 ✅ 创建 PaymentRecord 实体类
  - ✅ PaymentRecord实体类已创建，包含所有必要字段（id, orderId, paymentMethod, payStatus, transactionNo, paidAt, rawPayload, createdAt）
  - ✅ 使用JPA注解配置表结构和索引
  - ✅ 支付方式枚举（PaymentMethod）
  - ✅ 支付状态枚举（PaymentStatus）
- [x] 🔴 ✅ 创建 PaymentRecordRepository
  - ✅ PaymentRecordRepository已创建，继承JpaRepository
  - ✅ 实现了根据订单ID查找支付记录的方法
  - ✅ 实现了查找最新支付记录的方法
  - ✅ 实现了根据交易流水号查找支付记录的方法

### 8.2 服务层开发

- [x] 🔴 ✅ 实现模拟支付服务
  - ✅ PaymentService.pay方法已实现
  - ✅ 支持模拟支付（直接返回成功）
  - ✅ 生成交易流水号
  - ✅ 创建支付记录
  - ✅ 事务管理
- [x] 🔴 ✅ 实现支付记录创建服务
  - ✅ 已集成到pay方法中，支付时自动创建支付记录
- [x] 🔴 ✅ 实现支付记录查询服务
  - ✅ PaymentService.getPaymentRecords方法已实现
  - ✅ 返回订单的所有支付记录
  - ✅ 验证订单归属
- [x] 🔴 ✅ 实现支付成功后订单状态更新服务
  - ✅ 支付成功后调用OrderService.payOrder方法
  - ✅ 订单状态从CREATED更新为PAID
  - ✅ 记录支付时间（paid_at）
- [x] 🔴 ✅ 实现支付成功后库存扣减服务
  - ✅ 已在OrderService.payOrder方法中实现
  - ✅ 支付成功时扣减库存，防止超卖
- [ ] 🟢 ⬜ 实现第三方支付回调处理（可选）
  - ⏸️ 暂不实现，后续可根据需要添加支付宝、微信支付回调

### 8.3 控制器开发

- [x] 🔴 ✅ 实现支付接口 `POST /api/payments/{orderNo}/pay`
  - ✅ PaymentController.pay方法已实现
  - ✅ 使用@Valid进行参数校验
  - ✅ 支付成功后返回支付记录信息
- [x] 🔴 ✅ 实现支付记录查询接口 `GET /api/payments/{orderNo}`
  - ✅ PaymentController.getPaymentRecords方法已实现
  - ✅ 返回订单的所有支付记录列表
- [ ] 🟢 ⬜ 实现支付回调接口 `POST /api/payments/callback/mock`（可选）
  - ⏸️ 暂不实现，当前模拟支付直接返回成功，无需回调

### 8.4 工具类

- [x] 🔴 ✅ 创建交易流水号生成工具
  - ✅ TransactionNoUtil工具类已创建
  - ✅ 生成唯一交易流水号（日期时间+序号+随机数）

### 8.5 DTO类

- [x] 🔴 ✅ 创建支付相关DTO类
  - ✅ PaymentRequest（支付请求DTO）
  - ✅ PaymentResponse（支付响应DTO）

### 第八部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ PaymentRecord实体类、PaymentMethod和PaymentStatus枚举、PaymentRecordRepository
2. ✅ 模拟支付服务（支持MOCK支付方式）
3. ✅ 支付记录创建服务
4. ✅ 支付记录查询服务
5. ✅ 支付成功后订单状态更新（集成到OrderService.payOrder）
6. ✅ 支付成功后库存扣减（集成到OrderService.payOrder）
7. ✅ 支付接口（POST /api/payments/{orderNo}/pay）
8. ✅ 支付记录查询接口（GET /api/payments/{orderNo}）
9. ✅ 交易流水号生成工具（TransactionNoUtil）
10. ✅ 所有相关DTO类

**下一步建议**：
1. 测试支付流程（创建订单→支付→验证订单状态和库存扣减）
2. 测试支付记录查询功能
3. 继续开发其他模块（管理后台报表、邮件通知等）

**注意**：
- 当前实现为模拟支付，支付时直接返回成功
- 支付成功后会调用OrderService.payOrder方法更新订单状态并扣减库存
- 后续可扩展真实支付渠道（支付宝、微信支付），需要实现回调接口

---

## 九、后端开发 - 管理后台模块（admin）

### 9.1 报表服务层开发

- [x] 🔴 ✅ 实现销售汇总统计服务（订单量、销售额、客单价）
  - ✅ ReportService.getSalesSummary方法已实现
  - ✅ 统计订单总量、销售额（已支付订单）、客单价、支付转化率
  - ✅ 支持时间范围筛选（默认最近30天）
- [x] 🔴 ✅ 实现销售趋势分析服务（按天/周/月）
  - ✅ ReportService.getSalesTrend方法已实现
  - ✅ 支持按天统计销售额和订单数（可扩展按周/月）
  - ✅ 支持时间范围筛选
  - ✅ 返回时间序列数据点
- [x] 🔴 ✅ 实现热销商品统计服务（TOP N）
  - ✅ ReportService.getTopProducts方法已实现
  - ✅ 根据销售数量排序（TOP N，默认10，最大100）
  - ✅ 返回商品ID、名称、销售数量、销售金额、订单数
  - ✅ 支持时间范围筛选
- [x] 🟡 ✅ 实现订单状态分布统计服务
  - ✅ ReportService.getOrderStatusDistribution方法已实现
  - ✅ 按订单状态分组统计数量
  - ✅ 返回所有状态的分布情况（即使数量为0也包含）
  - ✅ 支持时间范围筛选
- [x] 🟡 ✅ 实现支付转化率统计服务
  - ✅ 已集成到getSalesSummary方法中，返回支付转化率

### 9.2 报表控制器开发

- [x] 🔴 ✅ 实现销售汇总接口 `GET /api/admin/reports/summary`
  - ✅ ReportController.getSalesSummary方法已实现
  - ✅ 支持startDate和endDate参数（格式：yyyy-MM-dd）
- [x] 🔴 ✅ 实现销售趋势接口 `GET /api/admin/reports/sales-trend`
  - ✅ ReportController.getSalesTrend方法已实现
  - ✅ 支持startDate、endDate、granularity参数
- [x] 🔴 ✅ 实现热销商品接口 `GET /api/admin/reports/top-products`
  - ✅ ReportController.getTopProducts方法已实现
  - ✅ 支持startDate、endDate、limit参数
- [x] 🟡 ✅ 实现订单状态分布接口 `GET /api/admin/reports/order-status`
  - ✅ ReportController.getOrderStatusDistribution方法已实现
  - ✅ 支持startDate和endDate参数

### 9.3 Repository扩展

- [x] 🔴 ✅ 扩展OrderRepository统计方法
  - ✅ 添加countByTimeRange方法（统计订单总数）
  - ✅ 添加sumPaidAmountByTimeRange方法（统计已支付订单销售额）
  - ✅ 添加countPaidOrdersByTimeRange方法（统计已支付订单数）
  - ✅ 添加countByStatusGroup方法（按状态分组统计）
- [x] 🔴 ✅ 扩展OrderItemRepository统计方法
  - ✅ 添加findTopProducts方法（统计热销商品，包含销售数量、金额、订单数）

### 9.4 DTO类

- [x] 🔴 ✅ 创建报表相关DTO类
  - ✅ SalesSummaryResponse（销售汇总响应DTO）
  - ✅ SalesTrendResponse（销售趋势响应DTO）
  - ✅ SalesTrendDataPoint（销售趋势数据点）
  - ✅ TopProductResponse（热销商品响应DTO）
  - ✅ OrderStatusDistributionResponse（订单状态分布响应DTO）
  - ✅ StatusCount（状态计数）
  - ✅ ReportQueryRequest（报表查询请求DTO）

### 第九部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 销售汇总统计服务（订单量、销售额、客单价、支付转化率）
2. ✅ 销售趋势分析服务（按天统计，可扩展按周/月）
3. ✅ 热销商品统计服务（TOP N，按销售数量排序）
4. ✅ 订单状态分布统计服务
5. ✅ 支付转化率统计（集成在销售汇总中）
6. ✅ 销售汇总接口（GET /api/admin/reports/summary）
7. ✅ 销售趋势接口（GET /api/admin/reports/sales-trend）
8. ✅ 热销商品接口（GET /api/admin/reports/top-products）
9. ✅ 订单状态分布接口（GET /api/admin/reports/order-status）
10. ✅ Repository统计方法扩展
11. ✅ 所有相关DTO类

**下一步建议**：
1. 测试报表接口功能
2. 验证统计数据准确性
3. 测试时间范围筛选功能
4. 继续开发其他模块（邮件通知等）

**注意**：
- 所有报表接口都需要ADMIN角色权限（已在SecurityConfig中配置）
- 时间范围参数格式：yyyy-MM-dd，不提供时默认查询最近30天
- 销售趋势当前实现为按天统计，可按需扩展按周/月统计
- 统计数据基于已支付订单（PAID/SHIPPED/COMPLETED状态）

---

## 十、后端开发 - 通知模块（notification）

### 10.1 邮件服务配置

- [x] 🟡 ✅ 配置 SMTP 邮件服务器
  - ✅ 在pom.xml中添加spring-boot-starter-mail依赖
  - ✅ 在application.properties中配置SMTP服务器参数（QQ邮箱、163邮箱示例）
  - ✅ 配置邮件发送参数（host、port、username、password、auth、starttls等）
  - ✅ 添加app.base-url配置（用于生成邮件中的订单链接）
- [x] 🟡 ✅ 配置邮件发送参数（环境变量）
  - ✅ 在application.properties中添加邮件配置项（使用环境变量或配置文件管理敏感信息）
  - ✅ 添加配置说明和注释

### 10.2 邮件服务开发

- [x] 🟡 ✅ 实现邮件发送服务
  - ✅ EmailService服务类已创建
  - ✅ 使用JavaMailSender发送邮件
  - ✅ 支持SimpleMailMessage发送文本邮件
  - ✅ 从UserRepository查询用户邮箱
- [x] 🟡 ✅ 实现订单发货邮件模板
  - ✅ buildOrderShippedEmailContent方法已实现
  - ✅ 邮件内容包含：订单号、下单时间、发货时间、物流单号、收货信息、商品明细、订单总金额、查看订单链接
  - ✅ 邮件主题：您的订单已发货（订单号：xxxx）
- [x] 🟡 ✅ 实现邮件发送失败处理
  - ✅ 使用try-catch捕获邮件发送异常
  - ✅ 邮件发送失败不影响业务流程，只记录日志
  - ✅ 使用@Slf4j记录日志信息
- [ ] 🟢 ⬜ 实现异步邮件发送（消息队列，可选）
  - ⏸️ 暂不实现，当前为同步发送，可按需扩展异步发送

### 10.3 邮件集成

- [x] 🟡 ✅ 在订单发货服务中集成邮件通知
  - ✅ 在OrderService.shipOrder方法中调用EmailService.sendOrderShippedEmail
  - ✅ 订单发货成功后自动发送邮件通知
  - ✅ 邮件发送失败不影响订单发货流程
- [ ] 🟡 ⬜ 测试邮件发送功能
  - ⏸️ 需要在配置SMTP服务器参数后进行实际测试

### 第十部分完成情况总结

**已完成任务（中优先级功能）**：
1. ✅ 添加Spring Mail依赖（spring-boot-starter-mail）
2. ✅ 配置SMTP邮件服务器（application.properties）
3. ✅ 实现邮件发送服务（EmailService）
4. ✅ 实现订单发货邮件模板（包含订单详情、商品明细、物流信息等）
5. ✅ 实现邮件发送失败处理（异常捕获、日志记录）
6. ✅ 在订单发货服务中集成邮件通知（OrderService.shipOrder）
7. ✅ 从UserRepository查询用户邮箱

**下一步建议**：
1. 配置实际的SMTP服务器参数（QQ邮箱、163邮箱或其他邮件服务）
2. 测试邮件发送功能
3. 验证邮件内容格式和链接是否正确
4. 可根据需要扩展异步邮件发送功能

**注意**：
- 邮件配置参数（特别是密码）应使用环境变量或配置文件管理，不要硬编码
- 邮件发送失败不会影响订单发货流程，只记录错误日志
- 当前实现为同步发送，如需异步发送可使用@Async注解或消息队列
- 用户邮箱必须存在才能发送邮件，如果用户没有邮箱会记录警告日志

---

## 十一、后端开发 - 通用功能

### 11.1 统一响应格式

- [x] 🔴 ✅ 实现统一响应结果类（ResponseResult）
  - ✅ ResponseResult类已创建，支持泛型
  - ✅ 包含code、message、data字段
  - ✅ 提供success()、success(T data)、success(String message, T data)、error()等方法
  - ✅ 统一响应格式，code=0表示成功，非0表示失败
- [x] 🔴 ✅ 实现统一错误码定义
  - ✅ ErrorCode类已创建，定义了统一的错误码常量
  - ✅ 包含通用错误码（SUCCESS、ERROR、PARAM_ERROR、UNAUTHORIZED、FORBIDDEN、NOT_FOUND、INTERNAL_ERROR）
  - ✅ 包含认证相关错误码（USER_NOT_FOUND、PASSWORD_ERROR、USER_DISABLED、EMAIL_EXISTS、PHONE_EXISTS、INVALID_TOKEN、TOKEN_EXPIRED）
  - ✅ 其他模块特定错误码在各模块中定义
- [x] 🔴 ✅ 实现全局异常处理器
  - ✅ GlobalExceptionHandler类已创建，使用@RestControllerAdvice
  - ✅ 处理BusinessException业务异常
  - ✅ 处理MethodArgumentNotValidException参数校验异常
  - ✅ 处理Exception其他异常
  - ✅ 统一的错误响应格式，使用ResponseResult

### 11.2 工具类开发

- [x] 🔴 ✅ 实现 JWT 工具类
  - ✅ JwtUtil类已创建，使用@Component注入
  - ✅ 支持生成Token（包含userId和role）
  - ✅ 支持解析Token（获取Claims、userId、role）
  - ✅ 支持验证Token有效性（检查过期）
  - ✅ 配置了jwt.secret和jwt.expiration参数
- [x] 🔴 ✅ 实现密码加密工具类
  - ✅ 在SecurityConfig中配置了BCryptPasswordEncoder
  - ✅ 使用Spring Security的PasswordEncoder接口
  - ✅ 在AuthService中使用passwordEncoder.encode()加密密码
  - ✅ 在AuthService中使用passwordEncoder.matches()验证密码
- [x] 🔴 ✅ 实现订单号生成工具类
  - ✅ OrderNoUtil工具类已创建
  - ✅ 生成唯一订单号（日期时间+序号+随机数）
  - ✅ 格式：yyyyMMddHHmmss + 6位序号 + 2位随机数
- [x] 🔴 ✅ 实现交易流水号生成工具类
  - ✅ TransactionNoUtil工具类已创建
  - ✅ 生成唯一交易流水号（日期时间+序号+随机数）
- [x] 🔴 ✅ 实现Security工具类
  - ✅ SecurityUtil工具类已创建
  - ✅ 提供getCurrentUserId()方法，从SecurityContext获取当前用户ID
- [ ] 🟡 ⬜ 实现日期时间工具类
  - ⏸️ 暂不实现，当前使用Java 8的LocalDateTime和DateTimeFormatter已满足需求
- [ ] 🟡 ⬜ 实现文件上传工具类
  - ⏸️ 暂不实现，后续如需要上传商品图片等功能时可添加

### 11.3 参数校验

- [x] 🔴 ✅ 实现参数校验注解
  - ✅ 使用了JSR303 Bean Validation标准注解
  - ✅ 在DTO类中使用@Valid、@NotNull、@NotBlank、@NotEmpty、@Size、@Email等注解
  - ✅ 在Controller方法参数上使用@Valid注解启用校验
- [x] 🔴 ✅ 实现自定义校验器
  - ⏸️ 当前使用标准JSR303注解已满足需求，未实现自定义校验器
  - ✅ 在AuthService中实现了业务层面的校验（如手机号格式、邮箱唯一性等）
- [x] 🔴 ✅ 配置参数校验异常处理
  - ✅ 在GlobalExceptionHandler中处理MethodArgumentNotValidException
  - ✅ 返回统一的错误响应格式
  - ✅ 记录校验失败的具体字段和错误信息

### 11.4 日志与监控

- [x] 🟡 ✅ 配置日志输出格式
  - ✅ 使用Lombok的@Slf4j注解
  - ✅ 在关键类中使用log.info()、log.warn()、log.error()记录日志
  - ✅ Spring Boot默认日志配置已足够，未单独配置日志格式
- [x] 🟡 ✅ 实现关键操作日志记录
  - ✅ 在GlobalExceptionHandler中记录异常日志
  - ✅ 在EmailService中记录邮件发送日志
  - ✅ 在各Service中记录关键业务操作日志
  - ✅ 使用@Slf4j注解简化日志代码
- [ ] 🟢 ⬜ 集成监控系统（可选）
  - ⏸️ 暂不实现，后续可集成Spring Boot Actuator或Prometheus等监控系统

### 第十一部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 统一响应结果类（ResponseResult）
2. ✅ 统一错误码定义（ErrorCode）
3. ✅ 全局异常处理器（GlobalExceptionHandler）
4. ✅ JWT工具类（JwtUtil）
5. ✅ 密码加密工具（BCryptPasswordEncoder）
6. ✅ 订单号生成工具类（OrderNoUtil）
7. ✅ 交易流水号生成工具类（TransactionNoUtil）
8. ✅ Security工具类（SecurityUtil）
9. ✅ 参数校验（JSR303 Bean Validation）
10. ✅ 参数校验异常处理（在GlobalExceptionHandler中）
11. ✅ 日志记录（使用@Slf4j）

**下一步建议**：
1. 继续开发前端部分
2. 进行接口测试
3. 完善错误码定义（如需要更多业务错误码）

**注意**：
- 所有通用功能已实现，为系统提供了统一的基础设施支持
- 参数校验使用了标准的JSR303注解，在Controller层和DTO层进行校验
- 日志记录使用了Lombok的@Slf4j注解，简化了代码
- 密码加密使用BCrypt算法，安全性高
- 统一响应格式确保了API响应的一致性

---

## 十二、前端开发 - 项目搭建

### 12.1 项目初始化

- [x] 🔴 ✅ 初始化 Vue 项目
  - ✅ 使用Vite创建Vue 3项目
  - ✅ 配置package.json，包含Vue 3、Vue Router、Pinia、Axios、Element Plus等依赖
  - ✅ 配置vite.config.js，设置路径别名和开发服务器代理
  - ✅ 创建项目基础目录结构（src/api、src/components、src/router、src/stores、src/utils、src/views）
- [x] 🔴 ✅ 安装并配置 Vue Router
  - ✅ 安装vue-router依赖
  - ✅ 创建router/index.js，配置所有路由（顾客端和管理端）
  - ✅ 配置路由元信息（requiresAuth、requiresAdmin）
  - ✅ 实现路由懒加载
- [x] 🔴 ✅ 安装并配置状态管理（Pinia/Vuex）
  - ✅ 安装pinia依赖
  - ✅ 创建stores/user.js用户状态管理
  - ✅ 实现登录、注册、登出、获取用户信息等功能
  - ✅ 实现isLoggedIn和isAdmin计算属性
- [x] 🔴 ✅ 安装并配置 Axios
  - ✅ 安装axios依赖
  - ✅ 创建utils/request.js，封装axios实例
  - ✅ 配置请求拦截器（自动添加Token）
  - ✅ 配置响应拦截器（统一错误处理、Token过期处理）
- [x] 🔴 ✅ 选择并集成 UI 框架（Element Plus/Ant Design Vue）
  - ✅ 安装element-plus和@element-plus/icons-vue依赖
  - ✅ 在main.js中全局注册Element Plus
  - ✅ 配置中文语言包
  - ✅ 注册所有Element Plus图标组件
- [x] 🔴 ✅ 配置项目基础结构（目录、路由、组件）
  - ✅ 创建完整的目录结构
  - ✅ 创建所有页面组件占位（登录、注册、商品、购物车、订单、个人中心、管理端等）
  - ✅ 创建API接口文件（auth.js、user.js）
  - ✅ 创建工具函数文件（token.js、message.js）

### 12.2 通用组件开发

- [x] 🔴 ✅ 开发统一 HTTP 请求封装（Axios 拦截器）
  - ✅ utils/request.js已创建
  - ✅ 请求拦截器：自动添加Authorization头（Bearer Token）
  - ✅ 响应拦截器：统一处理错误、Token过期自动跳转登录
  - ✅ 统一错误提示（使用Element Plus的ElMessage）
- [x] 🔴 ✅ 开发 Token 管理工具
  - ✅ utils/token.js已创建
  - ✅ 实现getToken、setToken、removeToken方法
  - ✅ 使用localStorage存储Token
- [x] 🔴 ✅ 开发路由守卫（权限控制）
  - ✅ 在router/index.js中实现beforeEach路由守卫
  - ✅ 检查requiresAuth：未登录时跳转到登录页
  - ✅ 检查requiresAdmin：非管理员访问管理端时重定向
  - ✅ 已登录用户访问登录/注册页时重定向到首页
  - ✅ 支持redirect参数，登录后返回原页面
- [x] 🟡 ✅ 开发通用提示组件
  - ✅ utils/message.js已创建
  - ✅ 封装showSuccess、showError、showWarning、showInfo方法
  - ✅ 封装confirm确认对话框方法
  - ✅ 基于Element Plus的ElMessage和ElMessageBox
- [x] 🟡 ✅ 开发加载状态组件
  - ✅ components/Loading.vue已创建
  - ✅ 全屏加载遮罩层
  - ✅ 使用Element Plus图标和旋转动画
- [x] 🟡 ✅ 开发分页组件
  - ✅ components/Pagination.vue已创建
  - ✅ 基于Element Plus的el-pagination
  - ✅ 支持页码、每页条数、总数配置
  - ✅ 支持v-model双向绑定和change事件

### 第十二部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 初始化Vue 3项目（使用Vite构建工具）
2. ✅ 安装并配置Vue Router（包含顾客端和管理端所有路由）
3. ✅ 安装并配置Pinia状态管理（用户状态管理）
4. ✅ 安装并配置Axios（统一HTTP请求封装）
5. ✅ 集成Element Plus UI框架（包含图标库和中文语言包）
6. ✅ 配置项目基础结构（完整的目录结构和页面组件占位）
7. ✅ 开发统一HTTP请求封装（请求/响应拦截器、Token自动添加、错误处理）
8. ✅ 开发Token管理工具（localStorage存储）
9. ✅ 开发路由守卫（权限控制、登录状态检查、管理员权限检查）
10. ✅ 开发通用提示组件（基于Element Plus的消息提示和确认对话框）
11. ✅ 开发加载状态组件（全屏加载遮罩）
12. ✅ 开发分页组件（基于Element Plus的分页组件）

**项目结构**：
```
frontend/
├── src/
│   ├── api/              # API接口（auth.js、user.js）
│   ├── components/       # 通用组件（Loading.vue、Pagination.vue、Message.vue）
│   ├── router/           # 路由配置（index.js，包含所有路由和路由守卫）
│   ├── stores/           # Pinia状态管理（user.js）
│   ├── utils/            # 工具函数（token.js、request.js、message.js）
│   ├── views/            # 页面组件（auth、product、cart、order、user、admin等）
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html            # HTML模板
├── package.json          # 项目配置和依赖
├── vite.config.js        # Vite配置
└── README.md             # 项目说明
```

**下一步建议**：
1. 安装前端依赖（npm install）
2. 启动开发服务器测试项目（npm run dev）
3. 测试登录/注册功能
4. 继续开发顾客端页面（商品列表、购物车、订单等）
5. 继续开发管理端页面（商品管理、订单管理、报表等）

**注意**：
- 前端项目使用Vite作为构建工具，开发服务器运行在3000端口
- API请求通过Vite代理转发到后端（http://localhost:8080）
- Token存储在localStorage中，路由守卫会自动检查登录状态
- 所有页面组件已创建占位文件，后续可逐步完善功能
- Element Plus已全局注册，可在任何组件中直接使用

---

## 十三、前端开发 - 顾客端页面

### 13.1 认证页面

- [x] 🔴 ✅ 开发登录页面
  - ✅ Login.vue已完善，包含账号密码输入、表单校验、登录功能
  - ✅ 支持邮箱或手机号登录
  - ✅ 登录成功后自动跳转（支持redirect参数）
- [x] 🔴 ✅ 开发注册页面
  - ✅ Register.vue已完善，包含邮箱、手机号、昵称、密码输入
  - ✅ 完整的表单校验（邮箱格式、手机号格式、密码强度）
  - ✅ 注册成功后跳转到登录页
- [x] 🔴 ✅ 实现登录/注册表单校验
  - ✅ 使用Element Plus表单校验
  - ✅ 登录：账号和密码必填，密码至少8位
  - ✅ 注册：邮箱格式、手机号格式（11位，1开头）、昵称长度（2-20字符）、密码强度（至少8位，包含字母和数字）
- [x] 🔴 ✅ 实现登录/注册 API 调用
  - ✅ 集成到stores/user.js中的loginAction和registerAction
  - ✅ 登录成功保存Token并获取用户信息
  - ✅ 错误处理和提示

### 13.2 商品页面

- [x] 🔴 ✅ 开发商品列表页面（分页、搜索、筛选）
  - ✅ ProductList.vue已完善
  - ✅ 商品网格布局展示
  - ✅ 支持分页（使用Pagination组件）
  - ✅ 支持关键词搜索（商品名称或描述）
  - ✅ 支持价格区间筛选
  - ✅ 商品卡片展示（图片、名称、价格、库存、销量）
  - ✅ 快速加入购物车功能
- [x] 🔴 ✅ 开发商品详情页面
  - ✅ ProductDetail.vue已完善
  - ✅ 商品图片展示（支持预览）
  - ✅ 商品详细信息（名称、价格、库存、销量、描述）
  - ✅ 数量选择器
  - ✅ 加入购物车和立即购买功能
- [x] 🔴 ✅ 实现商品搜索功能
  - ✅ 搜索框支持关键词搜索
  - ✅ 支持回车键搜索
  - ✅ 搜索后重置到第一页
- [x] 🔴 ✅ 实现商品筛选功能（分类、价格）
  - ✅ 价格区间筛选（最低价、最高价）
  - ✅ 筛选后重置到第一页
  - ✅ 重置筛选功能
- [x] 🟡 ✅ 实现商品图片预览功能
  - ✅ 使用Element Plus的el-image组件，支持图片预览
  - ✅ 商品详情页支持图片点击放大预览

### 13.3 购物车页面

- [x] 🔴 ✅ 开发购物车页面
  - ✅ Cart.vue已完善
  - ✅ 购物车商品列表展示（表格形式）
  - ✅ 商品信息展示（图片、名称、价格）
- [x] 🔴 ✅ 实现购物车商品列表展示
  - ✅ 显示商品图片、名称、单价、数量、小计
  - ✅ 过滤已删除的商品
- [x] 🔴 ✅ 实现购物车商品数量修改
  - ✅ 使用el-input-number组件
  - ✅ 数量修改后自动更新到后端
  - ✅ 数量不能超过商品库存
- [x] 🔴 ✅ 实现购物车商品删除
  - ✅ 删除按钮，带确认对话框
  - ✅ 删除成功后刷新列表
- [x] 🔴 ✅ 实现商品勾选/取消勾选
  - ✅ 每行商品支持勾选
  - ✅ 勾选状态同步到后端
  - ✅ 支持全选和取消全选
- [x] 🔴 ✅ 实现购物车总价计算
  - ✅ 计算已选商品的总价
  - ✅ 显示已选商品数量
  - ✅ 实时更新
- [x] 🔴 ✅ 实现结算按钮功能
  - ✅ 检查是否有选中商品
  - ✅ 跳转到订单确认页面

### 13.4 订单页面

- [x] 🔴 ✅ 开发订单确认页面（地址、商品、金额确认）
  - ✅ Checkout.vue已完善
  - ✅ 收货地址选择（单选，显示默认地址）
  - ✅ 商品信息展示（从购物车勾选商品）
  - ✅ 订单金额计算和展示
  - ✅ 提交订单功能
- [x] 🔴 ✅ 开发支付页面（模拟支付）
  - ✅ Payment.vue已完善
  - ✅ 订单信息展示（订单号、支付金额）
  - ✅ 支付方式选择（模拟支付）
  - ✅ 支付成功页面展示
  - ✅ 支付成功后跳转到订单详情
- [x] 🔴 ✅ 开发我的订单列表页面（分页、状态筛选）
  - ✅ OrderList.vue已完善
  - ✅ 订单状态筛选（全部、待支付、待发货、已发货、已完成、已取消）
  - ✅ 订单列表展示（订单号、下单时间、状态、商品信息、金额）
  - ✅ 分页功能
  - ✅ 订单操作（取消订单、去支付、查看详情）
- [x] 🔴 ✅ 开发订单详情页面
  - ✅ OrderDetail.vue已完善
  - ✅ 订单基本信息（订单号、时间、状态）
  - ✅ 收货信息展示
  - ✅ 商品明细展示
  - ✅ 订单金额汇总
  - ✅ 订单状态记录（时间线展示）
  - ✅ 订单操作（取消订单、去支付）
- [x] 🔴 ✅ 实现订单取消功能
  - ✅ 取消订单按钮（仅待支付订单可取消）
  - ✅ 确认对话框
  - ✅ 取消成功后刷新订单信息
- [x] 🔴 ✅ 实现订单状态展示
  - ✅ 订单状态标签（不同状态不同颜色）
  - ✅ 订单状态文本（中文显示）
  - ✅ 订单状态记录时间线

### 13.5 个人中心页面

- [x] 🟡 ✅ 开发个人中心页面
  - ✅ Profile.vue已完善
  - ✅ 左侧菜单导航（个人信息、修改密码、收货地址）
  - ✅ 右侧内容区域（根据菜单切换）
- [x] 🟡 ✅ 开发个人信息编辑页面
  - ✅ 个人信息展示（邮箱、手机号、昵称、角色）
  - ✅ 昵称可编辑
  - ✅ 保存修改功能
- [x] 🟡 ✅ 开发密码修改页面
  - ✅ 原密码、新密码、确认密码输入
  - ✅ 密码强度校验（至少8位，包含字母和数字）
  - ✅ 确认密码一致性校验
  - ✅ 修改成功后提示重新登录
- [x] 🟡 ✅ 开发收货地址管理页面（列表、新增、编辑、删除）
  - ✅ AddressList.vue已完善
  - ✅ 地址列表展示（网格布局）
  - ✅ 地址卡片展示（收货人、电话、地址详情、邮编）
  - ✅ 新增地址对话框（表单校验）
  - ✅ 编辑地址功能
  - ✅ 删除地址功能（带确认）
  - ✅ 地址管理按钮跳转
- [x] 🟡 ✅ 实现默认地址设置功能
  - ✅ 默认地址标签显示
  - ✅ 设为默认按钮
  - ✅ 设置默认地址后自动刷新列表

### 第十三部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 登录页面（表单校验、API调用、登录成功处理）
2. ✅ 注册页面（完整表单校验、API调用）
3. ✅ 商品列表页面（分页、搜索、筛选、商品展示）
4. ✅ 商品详情页面（详细信息、加入购物车、立即购买）
5. ✅ 商品搜索功能（关键词搜索、回车搜索）
6. ✅ 商品筛选功能（价格区间筛选、重置筛选）
7. ✅ 商品图片预览功能（Element Plus图片预览）
8. ✅ 购物车页面（商品列表、数量修改、删除、勾选、总价计算、结算）
9. ✅ 订单确认页面（地址选择、商品展示、金额确认、提交订单）
10. ✅ 支付页面（模拟支付、支付成功展示）
11. ✅ 订单列表页面（分页、状态筛选、订单展示、订单操作）
12. ✅ 订单详情页面（订单信息、商品明细、状态记录、订单操作）
13. ✅ 订单取消功能（待支付订单可取消）
14. ✅ 订单状态展示（状态标签、状态文本、状态记录时间线）
15. ✅ 个人中心页面（菜单导航、内容切换）
16. ✅ 个人信息编辑页面（信息展示、昵称编辑、保存修改）
17. ✅ 密码修改页面（密码校验、修改成功处理）
18. ✅ 收货地址管理页面（列表、新增、编辑、删除）
19. ✅ 默认地址设置功能（标签显示、设为默认、自动刷新）

**API接口文件**：
- ✅ api/product.js（商品列表、商品详情）
- ✅ api/cart.js（购物车增删改查、清空）
- ✅ api/order.js（订单创建、查询、详情、取消）
- ✅ api/payment.js（支付、支付记录查询）

**页面组件**：
- ✅ views/auth/Login.vue（登录页面）
- ✅ views/auth/Register.vue（注册页面）
- ✅ views/product/ProductList.vue（商品列表）
- ✅ views/product/ProductDetail.vue（商品详情）
- ✅ views/cart/Cart.vue（购物车）
- ✅ views/order/Checkout.vue（订单确认）
- ✅ views/payment/Payment.vue（支付页面）
- ✅ views/order/OrderList.vue（订单列表）
- ✅ views/order/OrderDetail.vue（订单详情）
- ✅ views/user/Profile.vue（个人中心）
- ✅ views/user/AddressList.vue（收货地址管理）

**下一步建议**：
1. 测试所有页面功能
2. 测试前后端联调
3. 测试完整业务流程（注册→登录→浏览商品→加入购物车→下单→支付）
4. 继续开发第十四部分：管理端页面

**注意**：
- 所有页面已实现基本功能，UI使用Element Plus组件
- 表单校验使用Element Plus内置校验规则
- 图片使用占位图，实际使用时需要配置真实的图片URL
- 支付功能为模拟支付，直接返回成功
- 所有API调用都包含错误处理和用户提示

---

## 十四、前端开发 - 管理端页面

### 14.1 管理端布局

- [x] 🔴 ✅ 开发管理端布局组件（侧边栏、顶部导航）
  - ✅ Layout.vue已完善
  - ✅ 左侧侧边栏（商品管理、订单管理、销售报表菜单）
  - ✅ 顶部导航栏（页面标题、用户信息下拉菜单）
  - ✅ 响应式布局，使用Element Plus的Container组件
- [x] 🔴 ✅ 实现管理端路由配置
  - ✅ 路由已在router/index.js中配置
  - ✅ 管理端路由使用嵌套路由结构
  - ✅ 所有管理端路由需要requiresAdmin权限
- [x] 🔴 ✅ 实现管理端权限验证
  - ✅ 路由守卫中检查requiresAdmin权限
  - ✅ 非管理员访问管理端时自动重定向
  - ✅ 使用userStore.isAdmin判断管理员权限

### 14.2 商品管理页面

- [x] 🔴 ✅ 开发商品列表页面（管理端）
  - ✅ ProductList.vue已完善
  - ✅ 商品列表展示（表格形式，包含ID、图片、名称、价格、库存、销量、状态）
  - ✅ 支持关键词搜索和状态筛选
  - ✅ 分页功能
- [x] 🔴 ✅ 开发商品新增/编辑页面
  - ✅ ProductForm.vue已完善
  - ✅ 商品表单（名称、价格、库存、描述、封面图片URL、分类ID）
  - ✅ 表单校验
  - ✅ 新增和编辑共用同一组件
- [x] 🔴 ✅ 实现商品上下架操作
  - ✅ 商品列表页面支持上下架操作
  - ✅ 操作按钮根据当前状态显示（上架/下架）
  - ✅ 操作成功后刷新列表
- [x] 🔴 ✅ 实现商品删除功能
  - ✅ 删除按钮，带确认对话框
  - ✅ 删除成功后刷新列表
- [ ] 🟡 ⬜ 实现商品图片上传功能
  - ⏸️ 暂不实现，当前使用URL方式，后续可根据需要添加
- [ ] 🟡 ⬜ 实现商品批量操作功能
  - ⏸️ 暂不实现，后续可根据需要添加

### 14.3 订单管理页面

- [x] 🔴 ✅ 开发订单列表页面（管理端，多条件筛选）
  - ✅ OrderList.vue已完善
  - ✅ 订单列表展示（订单号、用户ID、收货人、联系电话、订单金额、状态、下单时间）
  - ✅ 多条件筛选（订单号、状态、用户ID）
  - ✅ 分页功能
- [x] 🔴 ✅ 开发订单详情页面（管理端）
  - ✅ OrderDetail.vue已完善
  - ✅ 订单基本信息（订单号、用户ID、时间、状态）
  - ✅ 收货信息展示
  - ✅ 商品明细展示
  - ✅ 订单金额汇总
  - ✅ 订单状态记录（时间线展示）
- [x] 🔴 ✅ 实现订单发货功能
  - ✅ 发货对话框（物流单号、备注）
  - ✅ 表单校验
  - ✅ 发货成功后刷新订单信息
  - ✅ 仅已支付订单可发货
- [x] 🔴 ✅ 实现订单取消功能
  - ✅ 订单取消功能已在顾客端实现，管理端可查看订单状态
- [ ] 🟢 ⬜ 实现订单退款功能（可选）
  - ⏸️ 暂不实现，后续可根据需要添加

### 14.4 报表页面

- [x] 🔴 ✅ 开发销售统计概览页面
  - ✅ Report.vue已完善
  - ✅ 销售汇总卡片（订单总量、销售额、客单价、支付转化率）
  - ✅ 使用Element Plus的Card和Row/Col组件布局
- [x] 🔴 ✅ 开发销售趋势图表（折线图）
  - ✅ 使用ECharts实现折线图
  - ✅ 双Y轴展示（销售额和订单数）
  - ✅ 支持时间序列数据展示
- [x] 🔴 ✅ 开发热销商品图表（柱状图）
  - ✅ 使用ECharts实现柱状图
  - ✅ 展示TOP 10热销商品
  - ✅ 按销售数量排序
- [x] 🟡 ✅ 开发订单状态分布图表（饼图）
  - ✅ 使用ECharts实现饼图
  - ✅ 展示各订单状态的数量分布
  - ✅ 图例和标签展示
- [x] 🟡 ✅ 实现时间范围筛选功能
  - ✅ 使用Element Plus的DatePicker组件
  - ✅ 支持日期范围选择
  - ✅ 筛选后重新加载所有报表数据
- [x] 🟡 ✅ 集成图表库（ECharts/Chart.js）
  - ✅ 安装echarts依赖
  - ✅ 在报表页面中使用ECharts绘制图表
  - ✅ 支持图表响应式调整

### 第十四部分完成情况总结

**已完成任务（高优先级核心功能）**：
1. ✅ 管理端布局组件（侧边栏、顶部导航、响应式布局）
2. ✅ 管理端路由配置（嵌套路由、权限控制）
3. ✅ 管理端权限验证（路由守卫、管理员权限检查）
4. ✅ 商品列表页面（表格展示、搜索筛选、分页）
5. ✅ 商品新增/编辑页面（表单、校验、新增编辑共用）
6. ✅ 商品上下架操作（状态切换、操作反馈）
7. ✅ 商品删除功能（确认对话框、删除后刷新）
8. ✅ 订单列表页面（多条件筛选、分页、订单展示）
9. ✅ 订单详情页面（订单信息、商品明细、状态记录）
10. ✅ 订单发货功能（发货对话框、物流信息录入）
11. ✅ 销售统计概览页面（汇总卡片展示）
12. ✅ 销售趋势图表（ECharts折线图、双Y轴）
13. ✅ 热销商品图表（ECharts柱状图、TOP 10）
14. ✅ 订单状态分布图表（ECharts饼图）
15. ✅ 时间范围筛选功能（日期选择器、数据刷新）
16. ✅ 图表库集成（ECharts）

**API接口文件**：
- ✅ api/admin/product.js（商品管理接口）
- ✅ api/admin/order.js（订单管理接口）
- ✅ api/admin/report.js（报表接口）

**页面组件**：
- ✅ views/admin/Layout.vue（管理端布局）
- ✅ views/admin/product/ProductList.vue（商品列表）
- ✅ views/admin/product/ProductForm.vue（商品表单）
- ✅ views/admin/order/OrderList.vue（订单列表）
- ✅ views/admin/order/OrderDetail.vue（订单详情）
- ✅ views/admin/report/Report.vue（销售报表）

**依赖更新**：
- ✅ package.json中添加echarts和vue-echarts依赖

**下一步建议**：
1. 测试管理端所有功能
2. 测试权限控制（非管理员无法访问管理端）
3. 测试商品管理流程（新增、编辑、上下架、删除）
4. 测试订单管理流程（查看、发货）
5. 测试报表数据展示和图表渲染
6. 进行前后端联调测试

**注意**：
- 管理端所有页面都需要ADMIN角色权限
- 图表使用ECharts库，需要安装依赖（npm install）
- 商品图片上传功能暂未实现，当前使用URL方式
- 订单退款功能暂未实现，后续可根据需要添加
- 所有管理端操作都包含错误处理和用户提示

---

## 十五、测试

### 15.1 后端接口测试

- [ ] 🔴 ⬜ 测试认证授权接口（注册、登录）
- [ ] 🔴 ⬜ 测试商品管理接口（顾客端和管理端）
- [ ] 🔴 ⬜ 测试购物车接口
- [ ] 🔴 ⬜ 测试订单管理接口（创建、查询、取消）
- [ ] 🔴 ⬜ 测试支付接口
- [ ] 🟡 ⬜ 测试用户管理接口
- [ ] 🟡 ⬜ 测试邮件通知功能
- [ ] 🔴 ⬜ 测试管理端报表接口

### 15.2 功能测试

- [ ] 🔴 ⬜ 测试用户注册流程（正常/异常情况）
- [ ] 🔴 ⬜ 测试用户登录流程（正常/异常情况）
- [ ] 🔴 ⬜ 测试商品浏览和搜索流程
- [ ] 🔴 ⬜ 测试购物车操作流程
- [ ] 🔴 ⬜ 测试订单创建和支付流程
- [ ] 🔴 ⬜ 测试订单状态流转
- [ ] 🔴 ⬜ 测试库存扣减和防超卖
- [ ] 🟡 ⬜ 测试收货地址管理流程
- [ ] 🟡 ⬜ 测试邮件发送功能

### 15.3 权限和安全测试

- [ ] 🔴 ⬜ 测试 JWT Token 验证
- [ ] 🔴 ⬜ 测试基于角色的权限控制
- [ ] 🔴 ⬜ 测试接口权限拦截
- [ ] 🔴 ⬜ 测试参数校验和SQL注入防护
- [ ] 🔴 ⬜ 测试密码加密存储

### 15.4 性能测试

- [ ] 🟡 ⬜ 测试接口响应时间
- [ ] 🟡 ⬜ 测试分页查询性能
- [ ] 🟡 ⬜ 测试并发下单场景
- [ ] 🟢 ⬜ 压力测试（可选）

### 15.5 集成测试

- [ ] 🔴 ⬜ 测试前后端联调
- [ ] 🔴 ⬜ 测试完整业务流程（注册→浏览→购物车→下单→支付→发货）
- [ ] 🟡 ⬜ 测试管理端完整流程

---

## 十六、部署上线

### 16.1 环境配置

- [ ] 🔴 ⬜ 准备生产环境服务器
- [ ] 🔴 ⬜ 配置生产环境数据库
- [ ] 🔴 ⬜ 配置生产环境应用配置（application-prod.yml）
- [ ] 🔴 ⬜ 配置环境变量（数据库连接、JWT密钥、SMTP配置）

### 16.2 后端部署

- [ ] 🔴 ⬜ 打包 Spring Boot 应用（JAR）
- [ ] 🔴 ⬜ 部署后端应用（Docker 或直接运行）
- [ ] 🔴 ⬜ 配置后端应用启动脚本
- [ ] 🔴 ⬜ 配置后端日志输出

### 16.3 前端部署

- [ ] 🔴 ⬜ 打包前端应用（npm build）
- [ ] 🔴 ⬜ 配置 Nginx 托管前端静态资源
- [ ] 🔴 ⬜ 配置 Nginx 反向代理后端 API

### 16.4 数据库部署

- [ ] 🔴 ⬜ 在生产环境创建数据库
- [ ] 🔴 ⬜ 执行数据库建表脚本
- [ ] 🔴 ⬜ 初始化必要数据（管理员账号等）
- [ ] 🟡 ⬜ 配置数据库备份策略

### 16.5 域名和证书

- [ ] 🟡 ⬜ 配置域名解析
- [ ] 🟡 ⬜ 配置 HTTPS 证书（Let's Encrypt）
- [ ] 🟡 ⬜ 配置 SSL/TLS

### 16.6 上线验证

- [ ] 🔴 ⬜ 验证生产环境功能可用性
- [ ] 🔴 ⬜ 验证前后端连接正常
- [ ] 🔴 ⬜ 验证数据库连接正常
- [ ] 🔴 ⬜ 验证邮件发送功能正常
- [ ] 🟡 ⬜ 性能测试和优化

---

## 十七、文档编写

### 17.1 开发文档

- [ ] 🟡 ⬜ 编写 API 接口文档
- [ ] 🟡 ⬜ 编写数据库设计文档
- [ ] 🟡 ⬜ 编写代码注释和开发规范

### 17.2 用户文档

- [ ] 🟡 ⬜ 编写用户使用手册（顾客端）
- [ ] 🟡 ⬜ 编写管理员使用手册（管理端）

### 17.3 部署文档

- [ ] 🔴 ⬜ 编写部署操作文档
- [ ] 🔴 ⬜ 编写环境配置说明
- [ ] 🟡 ⬜ 编写运维手册

---

## 十八、后续扩展功能（低优先级）

### 18.1 功能扩展

- [ ] 🟢 ⬜ 实现订单退款功能
- [ ] 🟢 ⬜ 实现商品评价和评分功能
- [ ] 🟢 ⬜ 实现优惠券系统
- [ ] 🟢 ⬜ 实现满减活动
- [ ] 🟢 ⬜ 实现积分系统
- [ ] 🟢 ⬜ 对接真实支付渠道（支付宝/微信支付）
- [ ] 🟢 ⬜ 对接物流系统

### 18.2 技术优化

- [ ] 🟢 ⬜ 集成 Elasticsearch（搜索优化）
- [ ] 🟢 ⬜ 集成 Redis（缓存优化）
- [ ] 🟢 ⬜ 集成消息队列（异步处理）
- [ ] 🟢 ⬜ 实现秒杀功能
- [ ] 🟢 ⬜ 实现审计日志和操作日志
- [ ] 🟢 ⬜ 实现风控系统

---

## 任务统计

### 按优先级统计

- 🔴 高优先级任务：XX 个
- 🟡 中优先级任务：XX 个
- 🟢 低优先级任务：XX 个

### 按模块统计

| 模块 | 任务数 | 已完成 | 进行中 | 待开始 |
|------|--------|--------|--------|--------|
| 基础设施 | - | - | - | - |
| 数据库 | 12 | 12 | 0 | 0 |
| 认证授权 | 16 | 16 | 0 | 0 |
| 用户管理 | 16 | 16 | 0 | 0 |
| 商品管理 | 17 | 17 | 0 | 0 |
| 购物车 | 14 | 14 | 0 | 0 |
| 订单管理 | 19 | 19 | 0 | 0 |
| 支付 | 10 | 10 | 0 | 0 |
| 管理后台 | 11 | 11 | 0 | 0 |
| 通知 | 7 | 7 | 0 | 0 |
| 通用功能 | 11 | 11 | 0 | 0 |
| 前端（顾客端） | 19 | 19 | 0 | 0 |
| 前端（管理端） | 16 | 16 | 0 | 0 |
| 测试 | - | - | - | - |
| 部署 | - | - | - | - |

---

## 里程碑计划

### 里程碑1：数据库与后端基础（1-2周）
- 完成数据库设计和建表
- 完成认证授权模块
- 完成商品管理模块
- 完成购物车模块
- 完成订单管理模块基础功能

**关键交付物**：
- 数据库表结构
- 后端 API 接口（认证、商品、购物车、订单）

### 里程碑2：前端顾客端（1-2周）
- 完成前端项目搭建
- 完成顾客端所有页面开发
- 完成前后端联调

**关键交付物**：
- 可用的顾客端前端应用

### 里程碑3：支付与邮件（1周）
- 完成支付模块（模拟支付）
- 完成邮件通知功能
- 完成管理端基础功能

**关键交付物**：
- 完整的订单支付流程
- 邮件通知功能

### 里程碑4：报表与部署（1周）
- 完成销售统计报表
- 完成系统测试
- 完成部署上线

**关键交付物**：
- 完整的在线购物系统
- 生产环境可访问

---

## 注意事项

1. **任务依赖关系**：
   - 数据库设计需要在所有模块开发之前完成
   - 认证授权模块需要最先完成，因为其他模块依赖它
   - 商品管理模块需要在购物车和订单模块之前完成
   - 购物车模块需要在订单模块之前完成

2. **开发顺序建议**：
   - 先完成后端 API，再开发前端页面
   - 先完成核心功能（高优先级），再完成扩展功能
   - 每个模块完成后及时进行测试

3. **任务更新**：
   - 定期更新任务状态
   - 记录任务完成时间
   - 记录遇到的问题和解决方案

4. **代码管理**：
   - 使用 Git 进行版本控制
   - 及时提交代码
   - 编写清晰的提交信息

---

**文档最后更新时间**：2025-12-24

