package com.example.OnlineShopping.service;

import com.example.OnlineShopping.entity.Order;
import com.example.OnlineShopping.entity.OrderItem;
import com.example.OnlineShopping.entity.User;
import com.example.OnlineShopping.repository.OrderItemRepository;
import com.example.OnlineShopping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 邮件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发送订单发货通知邮件
     */
    public void sendOrderShippedEmail(Order order, String trackingNumber) {
        try {
            // 获取订单项
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            // 构建邮件内容
            String subject = String.format("您的订单已发货（订单号：%s）", order.getOrderNo());
            String content = buildOrderShippedEmailContent(order, orderItems, trackingNumber);

            // 获取用户邮箱
            String userEmail = getUserEmail(order.getUserId());
            if (userEmail == null || userEmail.trim().isEmpty()) {
                log.warn("用户邮箱为空，无法发送邮件：订单号={}, 用户ID={}", order.getOrderNo(), order.getUserId());
                return;
            }

            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("订单发货邮件发送成功：订单号={}, 用户ID={}, 邮箱={}", order.getOrderNo(), order.getUserId(), userEmail);
        } catch (Exception e) {
            log.error("订单发货邮件发送失败：订单号={}, 用户ID={}", order.getOrderNo(), order.getUserId(), e);
            // 邮件发送失败不影响业务流程，只记录日志
        }
    }

    /**
     * 构建订单发货邮件内容
     */
    private String buildOrderShippedEmailContent(Order order, List<OrderItem> orderItems, String trackingNumber) {
        StringBuilder content = new StringBuilder();
        content.append("尊敬的用户，您好！\n\n");
        content.append("您的订单已发货，详情如下：\n\n");
        content.append("订单号：").append(order.getOrderNo()).append("\n");
        content.append("下单时间：").append(order.getCreatedAt().format(DATE_TIME_FORMATTER)).append("\n");
        content.append("发货时间：").append(order.getShippedAt().format(DATE_TIME_FORMATTER)).append("\n");
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            content.append("物流单号：").append(trackingNumber).append("\n");
        }
        content.append("\n");
        content.append("收货信息：\n");
        content.append("收货人：").append(order.getReceiverName()).append("\n");
        content.append("联系电话：").append(order.getReceiverPhone()).append("\n");
        content.append("收货地址：").append(order.getReceiverAddress()).append("\n");
        content.append("\n");
        content.append("商品明细：\n");
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            content.append(String.format("- %s x%d ￥%.2f\n", 
                    item.getProductName(), item.getQuantity(), item.getProductPrice()));
            totalAmount = totalAmount.add(item.getSubtotalAmount());
        }
        content.append("\n");
        content.append("订单总金额：￥").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("\n");
        content.append("您可以点击以下链接查看订单详情：\n");
        content.append(baseUrl).append("/orders/").append(order.getOrderNo()).append("\n");
        content.append("\n");
        content.append("感谢您的购买！\n");

        return content.toString();
    }

    /**
     * 获取用户邮箱
     */
    private String getUserEmail(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            return user.getEmail();
        }
        return null; // 无法获取用户邮箱
    }
}

