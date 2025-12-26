## 1. 文档信息

* 项目名称：在线购物系统（Online Shopping System）
* 技术栈：Spring Boot + Vue + MySQL
* 架构模式：前后端分离、RESTful API
* 部署形态：单体应用（可后续演进微服务）
* 文档版本：V1.0
* 作者/日期：Judy / 2025-12-24（可改）

---

## 2. 项目背景与目标

### 2.1 背景

实现一个可上线运行的在线购物网站，满足顾客购物流程与销售管理需求。

### 2.2 目标

1. 顾客端：完成从注册登录到下单支付、邮件通知、查看订单全过程。
2. 管理端：完成商品目录管理、订单管理、销售统计报表。
3. 技术目标：MySQL 持久化、Spring Boot 提供 API、Vue 实现交互页面、部署上线可访问。

---

## 3. 角色与权限

### 3.1 角色

* 顾客（Customer）
* 管理员（Admin/Staff）

### 3.2 权限范围

* 顾客：注册/登录/注销、浏览/搜索商品、购物车、下单支付、查看订单状态与历史
* 管理员：商品管理（增删改查）、订单管理（状态、发货、退款/取消）、销售报表（按天/月、热销商品、订单量/销售额）

---

## 4. 功能需求说明

## 4.1 顾客端功能

### 4.1.1 注册/登录/注销

* 注册：手机号/邮箱 + 密码（加密存储），可选昵称、收货地址
* 登录：账号+密码；登录成功返回 Token
* 注销：前端清除 Token；后端可选提供 token 黑名单/刷新机制（简化版本仅前端清除）

**校验要求**

* 密码强度：≥8 位，包含字母与数字（可配置）
* 邮箱格式校验、手机号格式校验
* 防重复注册：邮箱/手机号唯一

### 4.1.2 展示产品列表

* 商品列表分页、分类筛选、价格区间筛选
* 商品详情：名称、价格、库存、图片、描述、销量/上架状态等

### 4.1.3 购买流程

流程：**浏览/查询 → 加入购物车 → 提交订单 → 付款 → 邮件确认发货**

* 购物车：增减数量、删除、清空、选择部分商品结算
* 提交订单：确认收货信息、确认商品与数量、计算总价、生成订单
* 付款：对接支付（建议先做“模拟支付/沙箱支付”）
* 发货邮件：订单发货后发送邮件（包含订单号、商品摘要、物流/状态信息）

### 4.1.4 查看订单状态和历史

* 我的订单：按时间分页、按状态过滤（待支付/已支付/待发货/已发货/已完成/已取消/已退款）
* 订单详情：订单号、下单时间、收货信息、商品明细、支付信息、状态流转记录

---

## 4.2 管理端功能（销售管理）

### 4.2.1 商品目录管理（CRUD）

* 新增商品：名称、分类、价格、库存、描述、图片、上架状态
* 编辑商品：支持修改所有字段
* 删除商品：逻辑删除优先（避免影响历史订单）
* 商品上下架：上架可见、下架不可购买

### 4.2.2 订单管理

* 查询订单：按订单号/用户/状态/时间范围
* 更新订单状态：发货（录入物流信息）、取消、退款（可选）
* 查看订单详情与用户信息（脱敏）

### 4.2.3 销售统计报表

* 核心指标：订单量、销售额、客单价、支付转化（可简化）
* 维度：

    * 按天/周/月销售额与订单数
    * 热销商品 TOP N
    * 订单状态分布（待支付/已支付/已发货…）

---

## 5. 非功能需求

### 5.1 性能

* 列表分页：默认 20 条/页，支持最大 100 条/页
* 接口响应：一般接口 < 500ms（本地/测试环境），部署后视服务器调整

### 5.2 安全

* 密码加密：BCrypt
* 鉴权：JWT（Access Token）+ 可选 Refresh Token
* 权限控制：Spring Security（基于角色）
* 防护：基础限流（可选）、SQL 注入防护（ORM）、参数校验（JSR303）

### 5.3 可靠性

* 下单扣库存：防超卖（建议下单时锁库存/支付成功扣库存，二选一）
* 支付与订单状态一致性：通过“状态机”或严格状态流转约束

---

## 6. 系统架构设计

### 6.1 总体架构

* 前端（Vue）

    * 顾客端：商品、购物车、订单、个人中心
    * 管理端：商品管理、订单管理、报表
* 后端（Spring Boot）

    * REST API：用户、商品、购物车、订单、支付、邮件、报表
* 数据库（MySQL）

    * 用户、商品、订单、订单项、购物车、支付记录、物流/状态记录等
* 第三方

    * 邮件：SMTP（如 QQ 邮箱、163、企业邮箱、SendGrid 等）
    * 支付：模拟支付/第三方支付（可后续接入）

### 6.2 模块划分（后端）

* auth：注册登录、Token
* user：用户资料、地址
* product：商品、分类、库存
* cart：购物车
* order：订单、订单项、状态流转
* payment：支付记录、回调（模拟）
* admin：管理端接口（商品/订单/报表）
* notification：邮件通知

### 6.3 关键业务状态设计（订单状态）

建议状态流转：

* **CREATED（已创建/待支付）**
* **PAID（已支付/待发货）**
* **SHIPPED（已发货）**
* **COMPLETED（已完成）**
* **CANCELLED（已取消）**
* **REFUNDED（已退款，可选）**

规则示例：

* CREATED → PAID（支付成功）
* CREATED → CANCELLED（超时未支付或用户取消）
* PAID → SHIPPED（管理员发货）
* SHIPPED → COMPLETED（确认收货/自动完成）
* PAID → REFUNDED（可选，退款成功）

---

## 7. 数据库设计（MySQL）

> 建议使用 InnoDB、utf8mb4，主键使用 BIGINT，自增或雪花ID均可。

### 7.1 表结构概览

**用户与权限**

* `user`：顾客/管理员账户
* `user_address`：收货地址（可选）
* `role` / `user_role`（如需更细权限可加）

**商品**

* `product`：商品主表
* `product_category`：分类（可选）
* `product_image`：图片（可选）

**购物与订单**

* `cart_item`：购物车项
* `order`：订单主表
* `order_item`：订单明细
* `payment_record`：支付记录
* `order_status_log`：订单状态变更记录（方便追踪）
* `shipment`：物流信息（可选）

### 7.2 核心表字段建议（简版）

#### user

* id (PK)
* email (UK), phone (UK)
* password_hash
* nickname
* role（CUSTOMER/ADMIN，简化可放一列）
* status（NORMAL/BANNED）
* created_at, updated_at

#### product

* id (PK)
* name
* category_id（可空）
* price（DECIMAL(10,2)）
* stock（INT）
* description（TEXT）
* status（ON_SALE/OFF_SALE/DELETED）
* cover_image_url
* created_at, updated_at

#### cart_item

* id (PK)
* user_id (FK)
* product_id (FK)
* quantity
* checked（是否勾选结算）
* created_at, updated_at

#### order

* id (PK)
* order_no（UK，业务单号）
* user_id (FK)
* status（CREATED/PAID/SHIPPED/COMPLETED/CANCELLED…）
* total_amount（DECIMAL）
* pay_amount（DECIMAL）
* receiver_name、receiver_phone、receiver_address（下单快照）
* paid_at, shipped_at, completed_at
* created_at, updated_at

#### order_item

* id (PK)
* order_id (FK)
* product_id (FK)
* product_name（快照）
* product_price（快照）
* quantity
* subtotal_amount

#### payment_record

* id (PK)
* order_id (FK)
* payment_method（MOCK/ALIPAY/WECHAT…）
* pay_status（INIT/SUCCESS/FAILED）
* transaction_no（第三方流水号，模拟可生成）
* paid_at
* raw_payload（JSON，可选）
* created_at

#### order_status_log

* id (PK)
* order_id (FK)
* from_status
* to_status
* remark
* operator（user/admin）
* created_at

---

## 8. API 设计（RESTful）

### 8.1 统一约定

* Base URL：`/api`
* 返回结构：

    * `code`：0 成功，非 0 失败
    * `message`
    * `data`
* 鉴权：

    * 需要登录：`Authorization: Bearer <token>`
    * 管理端接口：要求 ADMIN 角色

---

### 8.2 顾客端 API（示例）

**认证**

* `POST /api/auth/register`
* `POST /api/auth/login`
* `POST /api/auth/logout`（可选）

**商品**

* `GET /api/products`（分页、关键词、分类、价格区间）
* `GET /api/products/{id}`

**购物车**

* `GET /api/cart`
* `POST /api/cart/items`（添加商品）
* `PUT /api/cart/items/{id}`（改数量/勾选）
* `DELETE /api/cart/items/{id}`
* `DELETE /api/cart/clear`

**订单**

* `POST /api/orders`（从购物车勾选项创建订单）
* `GET /api/orders`（分页+状态过滤）
* `GET /api/orders/{orderNo}`（详情）
* `POST /api/orders/{orderNo}/cancel`

**支付（模拟）**

* `POST /api/payments/{orderNo}/pay`（返回支付结果/跳转信息）
* （若做回调）`POST /api/payments/callback/mock`

---

### 8.3 管理端 API（示例）

**商品管理**

* `POST /api/admin/products`
* `PUT /api/admin/products/{id}`
* `DELETE /api/admin/products/{id}`（逻辑删除）
* `PUT /api/admin/products/{id}/status`（上下架）

**订单管理**

* `GET /api/admin/orders`
* `GET /api/admin/orders/{orderNo}`
* `PUT /api/admin/orders/{orderNo}/ship`（录入物流信息并变更状态）

**统计报表**

* `GET /api/admin/reports/summary?from=YYYY-MM-DD&to=YYYY-MM-DD`
* `GET /api/admin/reports/sales-trend?granularity=day|month`
* `GET /api/admin/reports/top-products?limit=10`

---

## 9. 前端页面设计（Vue）

### 9.1 顾客端页面

* 登录/注册
* 商品列表（搜索、筛选、分页）
* 商品详情
* 购物车
* 订单确认页（地址、金额确认）
* 支付页（模拟支付按钮）
* 我的订单（列表+详情+状态）

### 9.2 管理端页面

* 管理员登录
* 商品管理（列表、新增、编辑）
* 订单管理（列表、详情、发货）
* 报表（折线图：趋势、柱状图：top 商品、卡片：汇总）

### 9.3 前端工程建议

* Vue Router：路由与权限守卫（admin 路由校验角色）
* Pinia/Vuex：登录态、购物车状态
* Axios：统一拦截器（注入 token、统一错误处理）
* UI 框架：Element Plus / Ant Design Vue（二选一）

---

## 10. 关键流程设计

### 10.1 下单流程（含库存与金额）

1. 用户在购物车勾选商品 → 点击结算
2. 后端校验商品是否上架、库存是否足够
3. 生成订单与订单项（保存商品快照价格）
4. 状态置为 CREATED（待支付）

**库存策略（建议）**

* 简化：创建订单时不扣库存，支付成功扣库存；若支付失败/取消不影响库存
* 风险：可能出现并发超卖 → 可通过“支付时扣库存 + 乐观锁版本号/原子扣减”防护

### 10.2 支付流程（模拟/可扩展）

* 前端点击“立即支付”
* 后端生成 payment_record → 返回 SUCCESS（模拟）
* 将订单状态 CREATED → PAID
* 记录 paid_at

### 10.3 发货与邮件通知

* 管理员在订单管理点击“发货”
* 写入 shipment/log，订单 PAID → SHIPPED
* 调用邮件服务发送“发货通知邮件”

---

## 11. 邮件服务设计

### 11.1 邮件内容（建议字段）

* 主题：您的订单已发货（订单号：xxxx）
* 内容：收货人、订单号、商品列表摘要、发货时间、物流单号（如有）、查看订单链接

### 11.2 技术实现

* Spring Boot：`spring-boot-starter-mail`
* 配置：SMTP host/port/username/password（用环境变量注入）
* 发送策略：同步发送（简化）或异步队列（进阶：RabbitMQ/Kafka）

---

## 12. 部署上线方案

### 12.1 推荐部署结构（简单可用）

* Nginx：前端静态资源托管 + 反向代理后端 API
* 后端：Spring Boot Jar（Docker 或直接运行）
* 数据库：MySQL（Docker 或云数据库）
* 域名 + HTTPS：Let’s Encrypt 证书（可选但建议）

### 12.2 典型路径

* 前端打包：`dist/` 放到 Nginx
* 后端运行：`java -jar app.jar`
* Nginx 代理：

    * `/` → 前端静态
    * `/api/` → 后端服务地址

### 12.3 环境配置（最少）

* `application-prod.yml`

    * MySQL 连接
    * JWT 密钥
    * SMTP 配置
    * 日志级别
* 使用 `.env` 或服务器环境变量保存敏感信息

---

## 13. 测试计划（建议）

### 13.1 功能测试用例（关键）

* 注册：正常/重复/弱密码/非法邮箱
* 登录：正确/错误密码/禁用用户
* 商品列表：分页、筛选、搜索
* 购物车：加入、修改数量、删除、勾选结算
* 下单：库存不足、价格变动（以快照为准）、空购物车
* 支付：成功/失败（模拟）
* 订单状态：状态流转正确、历史可查
* 管理端：商品 CRUD、上下架、发货、报表查询

### 13.2 接口测试

* Postman/Apifox：覆盖核心 API
* 自动化（可选）：Spring Boot Test + MockMvc

---

## 14. 里程碑计划（可选）

1. 数据库与后端基础：用户、商品、购物车、订单（1~2 周）
2. 前端顾客端：商品/购物车/下单/订单页（1~2 周）
3. 支付模拟 + 邮件发送 + 管理端（1 周）
4. 报表 + 部署上线 + 完整测试（1 周）

---

## 15. 可扩展点（后续迭代）

* 多地址管理、优惠券、满减、积分
* 真实支付渠道与支付回调
* 物流对接
* 搜索优化（ES）
* 秒杀/高并发库存优化（Redis、消息队列）
* 审计日志、操作日志、风控

