# 在线购物系统设计文档

## 1. 文档信息

* 项目名称：在线购物系统（Online Shopping System）
* 文档类型：系统设计文档
* 文档版本：V1.0
* 编写日期：2025-12-24
* 技术栈：Spring Boot 4.0.1 + Vue 3 + MySQL 8.0
* 架构模式：前后端分离、RESTful API

---

## 2. 系统整体架构

### 2.1 架构概述

在线购物系统采用**前后端分离**的架构模式，整体分为三个主要层次：

```
┌─────────────────────────────────────────────────────────────┐
│                        用户层                                │
│  ┌──────────────┐              ┌──────────────┐            │
│  │   顾客端      │              │   管理端      │            │
│  │  (Vue 3)     │              │  (Vue 3)     │            │
│  └──────────────┘              └──────────────┘            │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ HTTP/HTTPS
                            │ RESTful API
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      应用服务层                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Spring Boot 后端服务 (REST API)              │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐      │   │
│  │  │ 认证   │ │ 用户   │ │ 商品   │ │ 订单   │      │   │
│  │  │ 授权   │ │ 管理   │ │ 管理   │ │ 管理   │      │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘      │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐      │   │
│  │  │ 购物车 │ │ 支付   │ │ 通知   │ │ 报表   │      │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘      │   │
│  └──────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            │ JPA/Hibernate                   │
│                            ▼                                 │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ JDBC
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      数据持久层                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              MySQL 8.0 数据库                        │   │
│  │  (InnoDB引擎, utf8mb4字符集)                        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ SMTP
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      第三方服务                               │
│  ┌──────────────┐              ┌──────────────┐            │
│  │   邮件服务    │              │   支付服务    │            │
│  │  (SMTP)      │              │  (模拟/真实)  │            │
│  └──────────────┘              └──────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 技术架构

#### 2.2.1 前端技术栈

* **框架**：Vue 3.4.0
* **路由**：Vue Router 4.2.5
* **状态管理**：Pinia 2.1.7
* **HTTP客户端**：Axios 1.6.2
* **UI组件库**：Element Plus 2.4.4
* **图表库**：ECharts 5.4.3
* **构建工具**：Vite 5.0.8

#### 2.2.2 后端技术栈

* **框架**：Spring Boot 4.0.1
* **Java版本**：Java 21
* **安全框架**：Spring Security
* **数据访问**：Spring Data JPA / Hibernate
* **认证方式**：JWT (JSON Web Token)
* **密码加密**：BCrypt
* **邮件服务**：Spring Boot Mail
* **参数校验**：Spring Validation (JSR303)
* **数据库**：MySQL 8.0+ (MySQL Connector/J)

#### 2.2.3 数据库

* **数据库类型**：MySQL 8.0+
* **存储引擎**：InnoDB
* **字符集**：utf8mb4
* **连接池**：HikariCP (Spring Boot默认)

### 2.3 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Nginx 反向代理                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  /          →  前端静态资源 (dist/)                  │   │
│  │  /api/**     →  后端API服务 (http://localhost:8080)  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                ┌───────────┴───────────┐
                │                       │
                ▼                       ▼
    ┌───────────────────┐   ┌───────────────────┐
    │  前端静态资源      │   │  Spring Boot应用   │
    │  (Nginx托管)      │   │  (JAR包运行)       │
    └───────────────────┘   └───────────────────┘
                                    │
                                    │ JDBC
                                    ▼
                        ┌───────────────────┐
                        │   MySQL数据库     │
                        │  (Docker/云服务)   │
                        └───────────────────┘
```

---

## 3. 系统模块设计

### 3.1 认证授权模块（auth）

#### 3.1.1 模块职责

负责用户注册、登录、注销以及基于JWT的身份认证和权限验证。

#### 3.1.2 核心组件

* **Controller层**：`AuthController`
  * `POST /api/auth/register` - 用户注册
  * `POST /api/auth/login` - 用户登录
  * `POST /api/auth/logout` - 用户注销（可选）

* **Service层**：`AuthService`
  * 用户注册逻辑（邮箱/手机号唯一性校验、密码强度验证）
  * 用户登录逻辑（密码验证、JWT Token生成）
  * 用户注销逻辑（Token清除）

* **工具类**：`JwtUtil`
  * JWT Token生成
  * JWT Token解析和验证
  * Token过期时间管理

* **安全过滤器**：`JwtAuthenticationFilter`
  * 拦截HTTP请求，提取JWT Token
  * 验证Token有效性
  * 设置Spring Security认证上下文

#### 3.1.3 安全配置

* **SecurityConfig**：Spring Security配置类
  * 配置公开接口（`/api/auth/**`、`/api/products/**`）
  * 配置需要认证的接口
  * 配置管理端接口需要ADMIN角色（`/api/admin/**`）
  * 配置CORS跨域支持
  * 配置密码编码器（BCrypt）

#### 3.1.4 数据流程

```
用户注册/登录
    │
    ▼
AuthController 接收请求
    │
    ▼
AuthService 处理业务逻辑
    │
    ├─→ 注册：验证邮箱/手机号唯一性 → 密码加密(BCrypt) → 保存用户
    └─→ 登录：验证密码 → 生成JWT Token → 返回Token
    │
    ▼
返回响应（包含Token）
    │
    ▼
前端存储Token（localStorage/sessionStorage）
    │
    ▼
后续请求携带Token（Authorization: Bearer <token>）
    │
    ▼
JwtAuthenticationFilter 验证Token
    │
    ▼
设置认证上下文 → 允许访问
```

---

### 3.2 用户管理模块（user）

#### 3.2.1 模块职责

负责用户个人信息管理、收货地址管理、密码修改等功能。

#### 3.2.2 核心组件

* **Controller层**：`UserController`
  * `GET /api/user/profile` - 获取当前用户信息
  * `PUT /api/user/profile` - 更新用户信息
  * `PUT /api/user/password` - 修改密码
  * `GET /api/user/addresses` - 获取收货地址列表
  * `POST /api/user/addresses` - 添加收货地址
  * `PUT /api/user/addresses/{id}` - 更新收货地址
  * `DELETE /api/user/addresses/{id}` - 删除收货地址
  * `PUT /api/user/addresses/{id}/default` - 设置默认地址

* **Service层**：`UserService`
  * 用户信息查询和更新
  * 密码修改（需验证原密码）
  * 收货地址CRUD操作
  * 默认地址管理

* **实体类**：
  * `User` - 用户实体（邮箱、手机号、密码哈希、昵称、角色、状态等）
  * `UserAddress` - 收货地址实体（收货人、电话、地址、是否默认等）

#### 3.2.3 数据模型

**User表字段**：
* `id` - 用户ID（主键）
* `email` - 邮箱（唯一）
* `phone` - 手机号（唯一）
* `password_hash` - 密码哈希值（BCrypt加密）
* `nickname` - 昵称
* `role` - 角色（CUSTOMER/ADMIN）
* `status` - 状态（NORMAL/BANNED）
* `created_at` / `updated_at` - 时间戳

**UserAddress表字段**：
* `id` - 地址ID（主键）
* `user_id` - 用户ID（外键）
* `receiver_name` - 收货人姓名
* `receiver_phone` - 收货人电话
* `province` / `city` / `district` - 省市区
* `detail_address` - 详细地址
* `is_default` - 是否默认地址
* `created_at` / `updated_at` - 时间戳

---

### 3.3 商品管理模块（product）

#### 3.3.1 模块职责

负责商品信息的展示、搜索、筛选以及管理端的商品CRUD操作。

#### 3.3.2 核心组件

* **Controller层**：
  * `ProductController`（顾客端）
    * `GET /api/products` - 商品列表（支持分页、搜索、筛选）
    * `GET /api/products/{id}` - 商品详情
  * `AdminProductController`（管理端）
    * `POST /api/admin/products` - 创建商品
    * `PUT /api/admin/products/{id}` - 更新商品
    * `DELETE /api/admin/products/{id}` - 删除商品（逻辑删除）
    * `PUT /api/admin/products/{id}/status` - 商品上下架

* **Service层**：`ProductService`
  * 商品列表查询（分页、搜索、分类筛选、价格区间筛选）
  * 商品详情查询
  * 商品创建、更新、删除
  * 商品上下架操作
  * 库存管理

* **实体类**：`Product`
  * 商品基本信息（名称、价格、库存、描述、图片等）
  * 商品状态（ON_SALE/OFF_SALE/DELETED）
  * 销售统计（sales_count）

#### 3.3.3 数据模型

**Product表字段**：
* `id` - 商品ID（主键）
* `name` - 商品名称
* `category_id` - 分类ID（可选）
* `price` - 价格（DECIMAL(10,2)）
* `stock` - 库存数量
* `description` - 商品描述（TEXT）
* `status` - 状态（ON_SALE/OFF_SALE/DELETED）
* `cover_image_url` - 封面图片URL
* `sales_count` - 销售数量
* `created_at` / `updated_at` - 时间戳

#### 3.3.4 查询功能

* **分页查询**：默认20条/页，最大100条/页
* **关键词搜索**：支持商品名称、描述模糊搜索
* **分类筛选**：按商品分类筛选
* **价格区间筛选**：支持价格范围查询
* **状态筛选**：顾客端仅显示上架商品（ON_SALE）

---

### 3.4 购物车模块（cart）

#### 3.4.1 模块职责

负责用户购物车的管理，包括商品添加、数量修改、删除、勾选等操作。

#### 3.4.2 核心组件

* **Controller层**：`CartController`
  * `GET /api/cart` - 获取购物车列表
  * `POST /api/cart/items` - 添加商品到购物车
  * `PUT /api/cart/items/{id}` - 更新购物车项（数量、勾选状态）
  * `DELETE /api/cart/items/{id}` - 删除购物车项
  * `DELETE /api/cart/clear` - 清空购物车

* **Service层**：`CartService`
  * 购物车商品添加（如果已存在则更新数量）
  * 购物车商品数量修改
  * 购物车商品删除
  * 购物车商品勾选/取消勾选
  * 购物车清空
  * 购物车总价计算

* **实体类**：`CartItem`
  * 用户ID、商品ID、数量、是否选中

#### 3.4.3 数据模型

**CartItem表字段**：
* `id` - 购物车项ID（主键）
* `user_id` - 用户ID（外键）
* `product_id` - 商品ID（外键）
* `quantity` - 数量
* `checked` - 是否选中（用于结算时选择）
* `created_at` / `updated_at` - 时间戳
* 唯一约束：`(user_id, product_id)` - 同一用户同一商品只能有一条记录

#### 3.4.4 业务逻辑

* **添加商品**：如果购物车中已存在该商品，则增加数量；否则创建新记录
* **数量校验**：不能超过商品库存
* **结算准备**：用户勾选商品后，系统计算选中商品总价，用于订单创建

---

### 3.5 订单管理模块（order）

#### 3.5.1 模块职责

负责订单的创建、查询、状态管理以及订单状态流转记录。

#### 3.5.2 核心组件

* **Controller层**：
  * `OrderController`（顾客端）
    * `POST /api/orders` - 创建订单（从购物车勾选商品）
    * `GET /api/orders` - 订单列表（分页、状态筛选）
    * `GET /api/orders/{orderNo}` - 订单详情
    * `POST /api/orders/{orderNo}/cancel` - 取消订单
  * `AdminOrderController`（管理端）
    * `GET /api/admin/orders` - 订单列表（多条件筛选）
    * `GET /api/admin/orders/{orderNo}` - 订单详情
    * `PUT /api/admin/orders/{orderNo}/ship` - 订单发货

* **Service层**：`OrderService`
  * 订单创建（校验库存、保存商品快照、生成订单号）
  * 订单查询（分页、多条件筛选）
  * 订单详情查询
  * 订单状态流转（CREATED → PAID → SHIPPED → COMPLETED）
  * 订单取消
  * 订单发货（更新状态、发送邮件通知）
  * 订单状态日志记录

* **实体类**：
  * `Order` - 订单主表
  * `OrderItem` - 订单项（商品快照）
  * `OrderStatusLog` - 订单状态变更日志

#### 3.5.3 数据模型

**Order表字段**：
* `id` - 订单ID（主键）
* `order_no` - 订单号（唯一，业务单号）
* `user_id` - 用户ID（外键）
* `status` - 订单状态（CREATED/PAID/SHIPPED/COMPLETED/CANCELLED/REFUNDED）
* `total_amount` - 订单总金额（DECIMAL(10,2)）
* `pay_amount` - 实付金额（DECIMAL(10,2)）
* `receiver_name` / `receiver_phone` / `receiver_address` - 收货信息（快照）
* `paid_at` / `shipped_at` / `completed_at` - 状态时间戳
* `created_at` / `updated_at` - 时间戳

**OrderItem表字段**：
* `id` - 订单项ID（主键）
* `order_id` - 订单ID（外键）
* `product_id` - 商品ID（外键）
* `product_name` - 商品名称（快照）
* `product_price` - 商品价格（快照）
* `product_image_url` - 商品图片URL（快照）
* `quantity` - 数量
* `subtotal_amount` - 小计金额

**OrderStatusLog表字段**：
* `id` - 日志ID（主键）
* `order_id` - 订单ID（外键）
* `from_status` - 原状态
* `to_status` - 新状态
* `remark` - 备注
* `operator` - 操作人
* `created_at` - 创建时间

#### 3.5.4 订单状态流转

```
CREATED (待支付)
    │
    ├─→ PAID (已支付/待发货) [支付成功]
    │       │
    │       ├─→ SHIPPED (已发货) [管理员发货]
    │       │       │
    │       │       └─→ COMPLETED (已完成) [确认收货/自动完成]
    │       │
    │       └─→ REFUNDED (已退款) [可选]
    │
    └─→ CANCELLED (已取消) [用户取消/超时未支付]
```

#### 3.5.5 关键业务逻辑

* **订单创建**：
  1. 从购物车获取勾选商品
  2. 校验商品库存和上架状态
  3. 保存商品价格快照（避免价格变动影响）
  4. 生成唯一订单号
  5. 创建订单和订单项
  6. 记录状态日志（CREATED）
  7. 清空购物车中已下单商品

* **库存管理**：
  * 创建订单时不扣库存（简化方案）
  * 支付成功时扣减库存
  * 订单取消时恢复库存（如果已扣减）

* **价格快照**：
  * 订单创建时保存商品价格、名称、图片等快照
  * 确保订单金额不受后续商品信息变更影响

---

### 3.6 支付模块（payment）

#### 3.6.1 模块职责

负责订单支付处理、支付记录管理。初期支持模拟支付，后续可扩展真实支付渠道。

#### 3.6.2 核心组件

* **Controller层**：`PaymentController`
  * `POST /api/payments/{orderNo}/pay` - 支付订单（模拟支付）
  * `GET /api/payments/{orderNo}` - 查询支付记录

* **Service层**：`PaymentService`
  * 支付处理（模拟支付逻辑）
  * 支付成功后更新订单状态（CREATED → PAID）
  * 支付成功后扣减商品库存
  * 支付记录创建和查询
  * 支付回调处理（可扩展）

* **实体类**：`PaymentRecord`
  * 支付记录信息（订单ID、支付方式、支付状态、交易流水号等）

#### 3.6.3 数据模型

**PaymentRecord表字段**：
* `id` - 支付记录ID（主键）
* `order_id` - 订单ID（外键）
* `payment_method` - 支付方式（MOCK/ALIPAY/WECHAT）
* `pay_status` - 支付状态（INIT/SUCCESS/FAILED）
* `transaction_no` - 交易流水号
* `paid_at` - 支付时间
* `raw_payload` - 原始回调数据（JSON，可选）
* `created_at` - 创建时间

#### 3.6.4 支付流程

```
用户点击支付
    │
    ▼
PaymentController 接收支付请求
    │
    ▼
PaymentService 处理支付
    │
    ├─→ 模拟支付：直接返回SUCCESS
    │   │
    │   ├─→ 创建支付记录（SUCCESS状态）
    │   ├─→ 更新订单状态（CREATED → PAID）
    │   ├─→ 记录支付时间（paid_at）
    │   └─→ 扣减商品库存
    │
    └─→ 真实支付（可扩展）：
        ├─→ 调用第三方支付接口
        ├─→ 返回支付跳转链接
        └─→ 等待支付回调
```

---

### 3.7 管理后台模块（admin）

#### 3.7.1 模块职责

为管理员提供商品管理、订单管理、用户管理、销售统计等管理功能。

#### 3.7.2 核心组件

* **Controller层**：
  * `AdminProductController` - 商品管理
  * `AdminOrderController` - 订单管理
  * `AdminUserController` - 用户管理
  * `ReportController` - 销售统计报表

* **Service层**：
  * `ProductService` - 商品管理业务逻辑
  * `OrderService` - 订单管理业务逻辑
  * `UserService` - 用户管理业务逻辑
  * `ReportService` - 报表统计业务逻辑

#### 3.7.3 功能模块

**3.7.3.1 商品管理**
* 商品CRUD操作
* 商品上下架
* 商品批量操作（可选）

**3.7.3.2 订单管理**
* 订单列表查询（多条件筛选：订单号、用户、状态、时间范围）
* 订单详情查看
* 订单发货（录入物流信息，更新状态为SHIPPED）
* 订单取消和退款处理

**3.7.3.3 用户管理**
* 用户列表查询（分页）
* 用户详情查看（信息脱敏）
* 用户状态管理（正常/禁用）

**3.7.3.4 销售统计报表**
* 核心指标统计：
  * 订单量、销售额、客单价、支付转化率
* 销售趋势分析：
  * 按天/周/月统计销售额和订单数（折线图）
* 热销商品统计：
  * TOP N 热销商品（柱状图）
* 订单状态分布：
  * 各状态订单数量统计（饼图）
* 时间范围筛选：
  * 支持自定义时间范围查询

#### 3.7.4 权限控制

* 所有管理端接口（`/api/admin/**`）需要ADMIN角色
* 通过Spring Security的`@PreAuthorize("hasRole('ADMIN')")`注解控制
* 前端路由守卫也会校验管理员权限

---

### 3.8 通知模块（notification）

#### 3.8.1 模块职责

负责系统通知的创建、查询和管理，主要用于订单状态变更通知（如订单发货通知）。

#### 3.8.2 核心组件

* **Controller层**：`NotificationController`
  * `GET /api/notifications` - 获取通知列表（分页、未读筛选）
  * `PUT /api/notifications/{id}/read` - 标记通知为已读
  * `PUT /api/notifications/read-all` - 标记所有通知为已读

* **Service层**：`NotificationService`
  * 通知创建（订单发货时自动创建）
  * 通知列表查询
  * 通知已读状态更新

* **实体类**：`Notification`
  * 通知信息（用户ID、类型、标题、内容、订单号、是否已读等）

#### 3.8.3 数据模型

**Notification表字段**：
* `id` - 通知ID（主键）
* `user_id` - 用户ID（外键）
* `type` - 通知类型（ORDER_STATUS/ORDER_SHIPPED）
* `title` - 通知标题
* `content` - 通知内容（TEXT）
* `order_no` - 订单号
* `order_id` - 订单ID（外键）
* `is_read` - 是否已读（0-未读，1-已读）
* `link` - 跳转链接
* `created_at` - 创建时间

#### 3.8.4 业务逻辑

* **订单发货通知**：
  * 管理员发货时，系统自动创建通知
  * 通知类型：ORDER_SHIPPED
  * 通知内容包含订单号、商品摘要、发货时间等
  * 同时发送邮件通知（通过EmailService）

---

### 3.9 邮件服务模块（email）

#### 3.9.1 模块职责

负责发送系统邮件，主要用于订单发货通知。

#### 3.9.2 核心组件

* **Service层**：`EmailService`
  * `sendOrderShippedEmail()` - 发送订单发货邮件
  * 邮件模板构建
  * SMTP连接管理

#### 3.9.3 配置

* **SMTP配置**（application.properties）：
  * `spring.mail.host` - SMTP服务器地址
  * `spring.mail.port` - SMTP端口
  * `spring.mail.username` - 邮箱账号
  * `spring.mail.password` - 邮箱密码（建议使用环境变量）
  * `spring.mail.properties.mail.smtp.auth` - 启用认证
  * `spring.mail.properties.mail.smtp.starttls.enable` - 启用TLS

#### 3.9.4 邮件内容

**订单发货邮件模板**：
* 主题：您的订单已发货（订单号：xxxx）
* 内容包含：
  * 收货人信息
  * 订单号
  * 商品列表摘要
  * 发货时间
  * 物流单号（如有）
  * 查看订单链接

---

### 3.10 报表统计模块（report）

#### 3.10.1 模块职责

负责销售数据的统计和分析，为管理员提供业务决策支持。

#### 3.10.2 核心组件

* **Controller层**：`ReportController`
  * `GET /api/admin/reports/summary` - 销售概览（核心指标）
  * `GET /api/admin/reports/sales-trend` - 销售趋势（按天/周/月）
  * `GET /api/admin/reports/top-products` - 热销商品TOP N
  * `GET /api/admin/reports/order-status-distribution` - 订单状态分布

* **Service层**：`ReportService`
  * 销售概览统计（订单量、销售额、客单价、支付转化率）
  * 销售趋势分析（时间序列数据）
  * 热销商品统计（按销量排序）
  * 订单状态分布统计

#### 3.10.3 统计指标

**核心指标**：
* 总订单量
* 总销售额
* 平均客单价（销售额/订单量）
* 支付转化率（已支付订单数/总订单数）

**销售趋势**：
* 按天/周/月统计销售额和订单数
* 支持时间范围筛选
* 返回时间序列数据（用于折线图）

**热销商品**：
* 按销量（sales_count）排序
* 支持TOP N查询（默认TOP 10）
* 返回商品名称、销量、销售额

**订单状态分布**：
* 统计各状态订单数量
* 返回状态名称和数量（用于饼图）

---

## 4. 前端模块设计

### 4.1 前端架构

前端采用Vue 3 + Vue Router + Pinia的架构模式，使用Element Plus作为UI组件库。

```
frontend/
├── src/
│   ├── api/              # API接口封装
│   │   ├── auth.js       # 认证接口
│   │   ├── product.js    # 商品接口
│   │   ├── cart.js       # 购物车接口
│   │   ├── order.js      # 订单接口
│   │   ├── payment.js    # 支付接口
│   │   ├── user.js       # 用户接口
│   │   ├── notification.js  # 通知接口
│   │   └── admin/        # 管理端接口
│   ├── components/       # 公共组件
│   │   ├── CustomerLayout.vue  # 顾客端布局
│   │   ├── Loading.vue   # 加载组件
│   │   ├── Message.vue   # 消息提示组件
│   │   └── Pagination.vue # 分页组件
│   ├── router/           # 路由配置
│   │   └── index.js      # 路由定义和守卫
│   ├── stores/           # 状态管理（Pinia）
│   │   └── user.js       # 用户状态
│   ├── utils/            # 工具函数
│   │   ├── request.js    # Axios封装
│   │   ├── token.js      # Token管理
│   │   └── message.js    # 消息提示
│   ├── views/            # 页面组件
│   │   ├── auth/         # 认证页面
│   │   ├── product/      # 商品页面
│   │   ├── cart/         # 购物车页面
│   │   ├── order/        # 订单页面
│   │   ├── payment/      # 支付页面
│   │   ├── user/         # 用户中心页面
│   │   ├── notification/ # 通知页面
│   │   └── admin/        # 管理端页面
│   └── styles/           # 样式文件
│       └── common.css    # 公共样式
```

### 4.2 顾客端页面

#### 4.2.1 认证页面
* **Login.vue** - 登录页面
* **Register.vue** - 注册页面

#### 4.2.2 商品页面
* **ProductList.vue** - 商品列表（搜索、筛选、分页）
* **ProductDetail.vue** - 商品详情

#### 4.2.3 购物车页面
* **Cart.vue** - 购物车管理（增删改、勾选、结算）

#### 4.2.4 订单页面
* **Checkout.vue** - 订单确认页（地址选择、商品确认、金额计算）
* **OrderList.vue** - 订单列表（分页、状态筛选）
* **OrderDetail.vue** - 订单详情
* **OrderStatistics.vue** - 订单统计（顾客端）

#### 4.2.5 支付页面
* **Payment.vue** - 支付页面（模拟支付）

#### 4.2.6 用户中心页面
* **Profile.vue** - 个人资料（信息修改、密码修改）
* **AddressList.vue** - 收货地址管理

#### 4.2.7 通知页面
* **NotificationList.vue** - 通知列表

### 4.3 管理端页面

#### 4.3.1 布局
* **Layout.vue** - 管理端布局（侧边栏导航）

#### 4.3.2 商品管理
* **ProductList.vue** - 商品列表（CRUD操作）
* **ProductForm.vue** - 商品表单（新增/编辑）

#### 4.3.3 订单管理
* **OrderList.vue** - 订单列表（多条件筛选）
* **OrderDetail.vue** - 订单详情（发货操作）
* **OrderStatistics.vue** - 订单统计

#### 4.3.4 用户管理
* **UserList.vue** - 用户列表
* **UserDetail.vue** - 用户详情

#### 4.3.5 报表页面
* **Report.vue** - 销售统计报表（图表展示）

### 4.4 路由设计

#### 4.4.1 路由守卫

* **认证守卫**：检查用户是否登录（`meta.requiresAuth`）
* **权限守卫**：检查用户是否为管理员（`meta.requiresAdmin`）
* **自动重定向**：根据用户角色自动跳转到对应首页

#### 4.4.2 路由配置

* **公开路由**：商品列表、商品详情、登录、注册
* **顾客端路由**：购物车、订单、个人中心、收货地址、通知
* **管理端路由**：商品管理、订单管理、用户管理、报表（需要ADMIN角色）

### 4.5 状态管理

#### 4.5.1 Pinia Store

* **user.js** - 用户状态管理
  * `userInfo` - 用户信息
  * `isLoggedIn` - 登录状态
  * `isAdmin` - 是否管理员
  * `login()` - 登录方法
  * `logout()` - 登出方法
  * `fetchUserInfo()` - 获取用户信息

### 4.6 API封装

#### 4.6.1 Axios配置

* **request.js** - Axios实例封装
  * 请求拦截器：自动添加JWT Token
  * 响应拦截器：统一错误处理、Token过期处理
  * Base URL配置

#### 4.6.2 API模块

* 按功能模块划分API文件
* 统一返回格式处理
* 错误处理封装

---

## 5. 数据库设计

### 5.1 数据库概述

* **数据库类型**：MySQL 8.0+
* **字符集**：utf8mb4
* **存储引擎**：InnoDB
* **主键策略**：BIGINT自增

### 5.2 核心表结构

#### 5.2.1 用户相关表

* **user** - 用户表
* **user_address** - 收货地址表

#### 5.2.2 商品相关表

* **product** - 商品表
* **category** - 商品分类表（可选）

#### 5.2.3 订单相关表

* **order** - 订单表
* **order_item** - 订单项表
* **order_status_log** - 订单状态日志表
* **cart_item** - 购物车表

#### 5.2.4 支付相关表

* **payment_record** - 支付记录表

#### 5.2.5 其他表

* **notification** - 通知表

### 5.3 索引设计

* **主键索引**：所有表都有主键索引
* **唯一索引**：邮箱、手机号、订单号等唯一字段
* **普通索引**：外键字段、查询频繁字段（status、created_at等）
* **联合索引**：多条件查询字段（如user_id + status）

### 5.4 数据完整性

* **外键约束**：通过应用层保证（JPA关系映射）
* **唯一性约束**：邮箱、手机号、订单号等
* **非空约束**：关键字段（如密码、订单号等）
* **逻辑删除**：商品、订单等采用逻辑删除（status字段）

---

## 6. API设计规范

### 6.1 RESTful API设计

* **Base URL**：`/api`
* **HTTP方法**：
  * GET - 查询
  * POST - 创建
  * PUT - 更新
  * DELETE - 删除

### 6.2 统一响应格式

```json
{
  "code": 0,           // 0-成功，非0-失败
  "message": "success", // 提示信息
  "data": {}           // 响应数据
}
```

### 6.3 分页响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "content": [],      // 数据列表
    "totalElements": 100,  // 总记录数
    "totalPages": 5,    // 总页数
    "page": 0,          // 当前页（从0开始）
    "size": 20          // 每页大小
  }
}
```

### 6.4 错误码设计

* `0` - 成功
* `400` - 请求参数错误
* `401` - 未认证（Token无效或过期）
* `403` - 无权限（角色不足）
* `404` - 资源不存在
* `500` - 服务器内部错误
* 自定义业务错误码（如1001-用户不存在）

### 6.5 认证方式

* **请求头**：`Authorization: Bearer <token>`
* **Token获取**：登录接口返回
* **Token有效期**：24小时（可配置）

---

## 7. 安全设计

### 7.1 认证安全

* **密码加密**：BCrypt算法（Spring Security默认）
* **JWT Token**：使用HS256算法，包含用户ID、角色等信息
* **Token过期**：24小时自动过期
* **Token刷新**：可扩展Refresh Token机制

### 7.2 权限控制

* **基于角色的访问控制（RBAC）**：
  * CUSTOMER - 顾客角色
  * ADMIN - 管理员角色
* **接口级权限**：Spring Security + `@PreAuthorize`注解
* **前端路由守卫**：防止未授权访问

### 7.3 数据安全

* **SQL注入防护**：使用JPA/Hibernate ORM，避免直接SQL拼接
* **XSS防护**：前端输入校验和转义
* **CSRF防护**：JWT无状态认证，无需CSRF Token
* **敏感信息加密**：密码、支付信息等加密存储
* **数据脱敏**：管理员查看用户信息时脱敏显示

### 7.4 参数校验

* **前端校验**：表单验证（Element Plus表单验证）
* **后端校验**：JSR303 Bean Validation（`@Valid`、`@NotNull`等注解）

---

## 8. 性能优化

### 8.1 数据库优化

* **索引优化**：为查询频繁字段建立索引
* **分页查询**：所有列表接口支持分页，避免一次性加载大量数据
* **查询优化**：避免N+1查询问题（使用JPA的`@EntityGraph`或`JOIN FETCH`）

### 8.2 接口优化

* **响应时间目标**：一般接口 < 500ms
* **分页大小限制**：默认20条/页，最大100条/页
* **数据量控制**：避免返回过大的数据对象

### 8.3 前端优化

* **路由懒加载**：使用动态import实现组件懒加载
* **图片优化**：商品图片使用CDN或压缩优化
* **请求防抖**：搜索输入框使用防抖处理

---

## 9. 部署方案

### 9.1 部署架构

```
Nginx (反向代理)
    ├─→ 前端静态资源 (dist/)
    └─→ 后端API (/api/** → http://localhost:8080)
            └─→ Spring Boot应用 (JAR包)
                    └─→ MySQL数据库
```

### 9.2 部署步骤

1. **前端构建**：
   ```bash
   cd frontend
   npm install
   npm run build
   # 生成 dist/ 目录
   ```

2. **后端打包**：
   ```bash
   mvn clean package
   # 生成 target/OnlineShopping-0.0.1-SNAPSHOT.jar
   ```

3. **数据库初始化**：
   * 执行 `schema.sql` 建表
   * 执行 `data.sql` 初始化数据（可选）

4. **Nginx配置**：
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       # 前端静态资源
       location / {
           root /path/to/frontend/dist;
           try_files $uri $uri/ /index.html;
       }
       
       # 后端API代理
       location /api/ {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

5. **启动后端**：
   ```bash
   java -jar OnlineShopping-0.0.1-SNAPSHOT.jar
   ```

### 9.3 环境配置

* **开发环境**：`application.properties`
* **生产环境**：使用环境变量或`application-prod.properties`
* **敏感配置**：数据库密码、JWT密钥、SMTP密码等使用环境变量

---

## 10. 扩展性设计

### 10.1 功能扩展

* **支付渠道扩展**：支持支付宝、微信支付等真实支付
* **物流对接**：对接第三方物流API
* **优惠券系统**：优惠券、满减、折扣等营销功能
* **积分系统**：用户积分、积分兑换
* **商品评价**：商品评价和评分功能
* **商品推荐**：基于用户行为的商品推荐算法

### 10.2 技术扩展

* **缓存优化**：引入Redis缓存热点数据
* **消息队列**：使用RabbitMQ/Kafka处理异步任务（邮件发送、订单处理等）
* **搜索引擎**：使用Elasticsearch优化商品搜索
* **高并发优化**：秒杀功能、库存扣减优化（Redis分布式锁）
* **微服务架构**：从单体应用演进到微服务（Spring Cloud）

---

## 11. 总结

在线购物系统采用前后端分离架构，后端使用Spring Boot提供RESTful API，前端使用Vue 3构建用户界面，数据库使用MySQL存储数据。系统包含认证授权、用户管理、商品管理、购物车、订单管理、支付、通知、报表等核心模块，支持顾客端和管理端两种角色，实现了完整的在线购物业务流程。

系统设计遵循RESTful API规范，采用JWT认证，基于角色的权限控制，具有良好的安全性和可扩展性。通过合理的数据库设计、索引优化、分页查询等手段，保证了系统的性能。系统支持部署上线，可扩展支持真实支付、物流对接等高级功能。

---

## 附录

### A. 技术栈版本

* Spring Boot: 4.0.1
* Java: 21
* Vue: 3.4.0
* MySQL: 8.0+
* Node.js: 推荐 18+

### B. 相关文档

* `Requirements.md` - 需求文档
* `Construction.md` - 项目构建文档
* `TodoList.md` - 任务清单

### C. 联系方式

* 项目仓库：[GitHub Repository URL]
* 问题反馈：[Issue Tracker URL]

