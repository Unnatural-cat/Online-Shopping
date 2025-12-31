package com.example.OnlineShopping.dto.user;

import com.example.OnlineShopping.dto.order.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情响应DTO（管理端）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String email;
    private String phone;
    private String nickname;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 统计信息
    private Long orderCount; // 订单总数
    private Long totalSpent; // 总消费金额（分）
    private Long completedOrderCount; // 已完成订单数
    
    // 购买记录（订单列表）
    private List<OrderResponse> orders;
    
    // 浏览记录（从订单商品中推断，显示用户购买过的商品）
    private List<BrowseRecord> browseRecords;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrowseRecord {
        private Long productId;
        private String productName;
        private String productImageUrl;
        private Integer viewCount; // 浏览次数（通过订单商品数量推断）
        private LocalDateTime lastViewTime; // 最后浏览时间（最后购买时间）
    }
}

