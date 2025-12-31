package com.example.OnlineShopping.service;

import com.example.OnlineShopping.common.ErrorCode;
import com.example.OnlineShopping.dto.order.CreateOrderRequest;
import com.example.OnlineShopping.dto.order.OrderItemResponse;
import com.example.OnlineShopping.dto.order.OrderListResponse;
import com.example.OnlineShopping.dto.order.OrderQueryRequest;
import com.example.OnlineShopping.dto.order.OrderResponse;
import com.example.OnlineShopping.dto.order.OrderStatusLogResponse;
import com.example.OnlineShopping.dto.order.ShipOrderRequest;
import com.example.OnlineShopping.entity.CartItem;
import com.example.OnlineShopping.entity.Order;
import com.example.OnlineShopping.entity.OrderItem;
import com.example.OnlineShopping.entity.OrderStatusLog;
import com.example.OnlineShopping.entity.Product;
import com.example.OnlineShopping.entity.UserAddress;
import com.example.OnlineShopping.enums.OrderStatus;
import com.example.OnlineShopping.enums.ProductStatus;
import com.example.OnlineShopping.exception.BusinessException;
import com.example.OnlineShopping.repository.CartItemRepository;
import com.example.OnlineShopping.repository.OrderItemRepository;
import com.example.OnlineShopping.repository.OrderRepository;
import com.example.OnlineShopping.repository.OrderStatusLogRepository;
import com.example.OnlineShopping.repository.ProductRepository;
import com.example.OnlineShopping.repository.UserAddressRepository;
import com.example.OnlineShopping.service.EmailService;
import com.example.OnlineShopping.util.OrderNoUtil;
import com.example.OnlineShopping.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusLogRepository orderStatusLogRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserAddressRepository userAddressRepository;
    private final CartService cartService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建订单
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 如果提供了地址ID，则从地址表获取地址信息
        String receiverName = request.getReceiverName();
        String receiverPhone = request.getReceiverPhone();
        String receiverAddress = request.getReceiverAddress();

        if (request.getAddressId() != null) {
            UserAddress address = userAddressRepository.findByIdAndUserId(request.getAddressId(), userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "收货地址不存在"));
            receiverName = address.getReceiverName();
            receiverPhone = address.getReceiverPhone();
            receiverAddress = buildFullAddress(address);
        }

        // 校验购物车商品
        cartService.validateCartItems(request.getCartItemIds());

        // 获取购物车项
        List<CartItem> cartItems = request.getCartItemIds().stream()
                .map(cartItemId -> cartItemRepository.findByIdAndUserId(cartItemId, userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "购物车项不存在")))
                .filter(item -> item.getChecked()) // 只处理勾选的商品
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请选择要结算的商品");
        }

        // 生成订单号
        String orderNo = OrderNoUtil.generateOrderNo();

        // 创建订单
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

            // 再次校验库存和状态
            if (product.getStatus() != ProductStatus.ON_SALE) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, 
                        String.format("商品【%s】已下架，无法下单", product.getName()));
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, 
                        String.format("商品【%s】库存不足", product.getName()));
            }

            // 创建订单项（保存商品快照，包括图片URL）
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .productImageUrl(product.getCoverImageUrl())
                    .quantity(cartItem.getQuantity())
                    .subtotalAmount(subtotal)
                    .build();
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalAmount(totalAmount)
                .payAmount(totalAmount)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .build();

        order = orderRepository.save(order);

        // 保存订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemRepository.save(orderItem);
        }

        // 记录状态日志
        recordStatusLog(order.getId(), null, OrderStatus.CREATED.name(), "订单创建", "user");
        
        // 创建订单创建通知
        try {
            notificationService.createOrderStatusNotification(
                    order.getUserId(), order.getOrderNo(), order.getId(), 
                    null, OrderStatus.CREATED.name(), "订单创建成功");
        } catch (Exception e) {
            log.error("创建订单通知失败：订单号={}", order.getOrderNo(), e);
        }

        // 删除购物车中的商品
        for (CartItem cartItem : cartItems) {
            cartItemRepository.delete(cartItem);
        }

        return convertToOrderResponse(order, true, true);
    }

    /**
     * 顾客端：查询订单列表
     */
    public OrderListResponse getOrders(OrderQueryRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        int pageSize = Math.min(request.getSize() != null ? request.getSize() : 20, 100);
        int pageNum = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<Order> orderPage;
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                OrderStatus status = OrderStatus.valueOf(request.getStatus().toUpperCase());
                orderPage = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
            } catch (IllegalArgumentException e) {
                orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
            }
        } else {
            orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return OrderListResponse.builder()
                .content(orderPage.getContent().stream()
                        .map(order -> convertToOrderResponse(order, true, false))
                        .collect(Collectors.toList()))
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }

    /**
     * 顾客端：查询订单详情
     */
    public OrderResponse getOrderDetail(String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该订单");
        }

        return convertToOrderResponse(order, true, true);
    }

    /**
     * 顾客端：取消订单
     */
    @Transactional
    public OrderResponse cancelOrder(String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该订单");
        }

        // 只能取消待支付的订单
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能取消待支付的订单");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        // 记录状态日志
        recordStatusLog(order.getId(), OrderStatus.CREATED.name(), OrderStatus.CANCELLED.name(), 
                "用户取消订单", "user");
        
        // 创建订单取消通知
        try {
            notificationService.createOrderStatusNotification(
                    order.getUserId(), order.getOrderNo(), order.getId(), 
                    OrderStatus.CREATED.name(), OrderStatus.CANCELLED.name(), "订单已取消");
        } catch (Exception e) {
            log.error("创建订单通知失败：订单号={}", order.getOrderNo(), e);
        }

        return convertToOrderResponse(order, true, true);
    }

    /**
     * 管理端：查询订单列表
     */
    public OrderListResponse getAdminOrders(OrderQueryRequest request) {
        int pageSize = Math.min(request.getSize() != null ? request.getSize() : 20, 100);
        int pageNum = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        OrderStatus status = null;
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                status = OrderStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效状态
            }
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (request.getStartTime() != null && !request.getStartTime().trim().isEmpty()) {
            try {
                startTime = LocalDateTime.parse(request.getStartTime() + " 00:00:00", DATE_FORMATTER);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        if (request.getEndTime() != null && !request.getEndTime().trim().isEmpty()) {
            try {
                endTime = LocalDateTime.parse(request.getEndTime() + " 23:59:59", DATE_FORMATTER);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        Page<Order> orderPage = orderRepository.findWithFilters(
                request.getOrderNo(), request.getUserId(), status, startTime, endTime, pageable);

        return OrderListResponse.builder()
                .content(orderPage.getContent().stream()
                        .map(order -> convertToOrderResponse(order, true, false))
                        .collect(Collectors.toList()))
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(pageNum + 1)
                .pageSize(pageSize)
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }

    /**
     * 管理端：查询订单详情
     */
    public OrderResponse getAdminOrderDetail(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        return convertToOrderResponse(order, true, true);
    }

    /**
     * 管理端：订单发货
     */
    @Transactional
    public OrderResponse shipOrder(String orderNo, ShipOrderRequest request) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        // 只能对已支付的订单发货
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能对已支付的订单进行发货");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // 记录状态日志
        String remark = "订单发货";
        if (request.getTrackingNumber() != null && !request.getTrackingNumber().trim().isEmpty()) {
            remark += "，物流单号：" + request.getTrackingNumber();
        }
        if (request.getRemark() != null && !request.getRemark().trim().isEmpty()) {
            remark += "，" + request.getRemark();
        }
        recordStatusLog(order.getId(), OrderStatus.PAID.name(), OrderStatus.SHIPPED.name(), 
                remark, "admin");

        // 发送发货邮件通知
        try {
            emailService.sendOrderShippedEmail(order, request.getTrackingNumber());
        } catch (Exception e) {
            // 邮件发送失败不影响业务流程，只记录日志
            log.error("订单发货邮件发送失败：订单号={}", order.getOrderNo(), e);
        }

        // 创建发货通知
        try {
            notificationService.createOrderShippedNotification(
                    order.getUserId(), order.getOrderNo(), order.getId(), request.getTrackingNumber());
        } catch (Exception e) {
            log.error("创建发货通知失败：订单号={}", order.getOrderNo(), e);
        }

        return convertToOrderResponse(order, true, true);
    }

    /**
     * 支付成功后更新订单状态为PAID（由支付模块调用）
     */
    @Transactional
    public void payOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单状态不正确，无法支付");
        }

        // 扣减库存
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "商品不存在"));

            if (product.getStock() < orderItem.getQuantity()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, 
                        String.format("商品【%s】库存不足", product.getName()));
            }

            product.setStock(product.getStock() - orderItem.getQuantity());
            productRepository.save(product);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // 记录状态日志
        recordStatusLog(order.getId(), OrderStatus.CREATED.name(), OrderStatus.PAID.name(), 
                "支付成功", "system");
        
        // 创建订单支付成功通知
        try {
            notificationService.createOrderStatusNotification(
                    order.getUserId(), order.getOrderNo(), order.getId(), 
                    OrderStatus.CREATED.name(), OrderStatus.PAID.name(), "订单支付成功");
        } catch (Exception e) {
            log.error("创建订单通知失败：订单号={}", order.getOrderNo(), e);
        }
    }

    /**
     * 记录订单状态日志
     */
    private void recordStatusLog(Long orderId, String fromStatus, String toStatus, String remark, String operator) {
        OrderStatusLog log = OrderStatusLog.builder()
                .orderId(orderId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .remark(remark)
                .operator(operator)
                .build();
        orderStatusLogRepository.save(log);
    }

    /**
     * 构建完整地址
     */
    private String buildFullAddress(UserAddress address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null) {
            sb.append(address.getCity());
        }
        if (address.getDistrict() != null) {
            sb.append(address.getDistrict());
        }
        if (address.getDetailAddress() != null) {
            sb.append(address.getDetailAddress());
        }
        return sb.toString();
    }

    /**
     * 转换为OrderResponse
     */
    private OrderResponse convertToOrderResponse(Order order, boolean includeItems, boolean includeStatusLogs) {
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .paidAt(order.getPaidAt())
                .shippedAt(order.getShippedAt())
                .completedAt(order.getCompletedAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        if (includeItems) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            
            // 检查是否需要查询商品信息（只有订单项中没有图片URL时才查询）
            boolean needQueryProducts = orderItems.stream()
                    .anyMatch(item -> {
                        try {
                            String imageUrl = item.getProductImageUrl();
                            return imageUrl == null || imageUrl.isEmpty();
                        } catch (Exception e) {
                            // 如果字段不存在（数据库还未迁移），需要查询
                            return true;
                        }
                    });
            
            final Map<Long, Product> productMap;
            if (needQueryProducts) {
                // 批量查询商品信息以获取图片URL（包括已删除的商品）
                List<Long> productIds = orderItems.stream()
                        .map(OrderItem::getProductId)
                        .distinct()
                        .collect(Collectors.toList());
                
                if (!productIds.isEmpty()) {
                    List<Product> products = productRepository.findAllById(productIds);
                    productMap = products.stream()
                            .collect(Collectors.toMap(Product::getId, product -> product));
                    
                    log.debug("查询订单商品图片: 订单ID={}, 需要查询的商品数={}, 实际查询到={}", 
                            order.getId(), productIds.size(), products.size());
                } else {
                    productMap = new java.util.HashMap<>();
                }
            } else {
                productMap = new java.util.HashMap<>();
            }
            
            // 转换为响应DTO
            List<OrderItemResponse> itemResponses = orderItems.stream()
                    .map(item -> convertToOrderItemResponse(item, productMap.get(item.getProductId())))
                    .collect(Collectors.toList());
            builder.items(itemResponses);
        }

        if (includeStatusLogs) {
            List<OrderStatusLog> statusLogs = orderStatusLogRepository.findByOrderIdOrderByCreatedAtDesc(order.getId());
            List<OrderStatusLogResponse> logResponses = statusLogs.stream()
                    .map(this::convertToOrderStatusLogResponse)
                    .collect(Collectors.toList());
            builder.statusLogs(logResponses);
        }

        return builder.build();
    }

    /**
     * 转换为OrderItemResponse
     */
    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem, Product product) {
        String productImageUrl = null;
        
        // 优先从订单项快照中获取（如果数据库字段存在且不为空）
        try {
            String savedImageUrl = orderItem.getProductImageUrl();
            if (savedImageUrl != null && !savedImageUrl.trim().isEmpty()) {
                productImageUrl = savedImageUrl.trim();
                log.debug("从订单项快照获取图片URL: 订单项ID={}, 商品ID={}, URL={}", 
                        orderItem.getId(), orderItem.getProductId(), productImageUrl);
            }
        } catch (Exception e) {
            // 如果字段不存在（数据库还未迁移），忽略异常，继续从商品表查询
            log.debug("订单项中未找到图片URL字段，将从商品表查询: 订单项ID={}", orderItem.getId());
        }
        
        // 如果订单项中没有，尝试从商品表中获取
        if ((productImageUrl == null || productImageUrl.isEmpty()) && product != null) {
            String productCoverImageUrl = product.getCoverImageUrl();
            if (productCoverImageUrl != null && !productCoverImageUrl.trim().isEmpty()) {
                productImageUrl = productCoverImageUrl.trim();
                log.debug("从商品表获取图片URL: 订单项ID={}, 商品ID={}, URL={}", 
                        orderItem.getId(), orderItem.getProductId(), productImageUrl);
            }
        }
        
        if (productImageUrl == null || productImageUrl.isEmpty()) {
            log.warn("无法获取商品图片URL: 订单项ID={}, 商品ID={}, 商品名={}", 
                    orderItem.getId(), orderItem.getProductId(), orderItem.getProductName());
        }
        
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .productImageUrl(productImageUrl)
                .productPrice(orderItem.getProductPrice())
                .quantity(orderItem.getQuantity())
                .subtotalAmount(orderItem.getSubtotalAmount())
                .createdAt(orderItem.getCreatedAt())
                .build();
    }

    /**
     * 转换为OrderStatusLogResponse
     */
    private OrderStatusLogResponse convertToOrderStatusLogResponse(OrderStatusLog log) {
        return OrderStatusLogResponse.builder()
                .id(log.getId())
                .fromStatus(log.getFromStatus())
                .toStatus(log.getToStatus())
                .remark(log.getRemark())
                .operator(log.getOperator())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

