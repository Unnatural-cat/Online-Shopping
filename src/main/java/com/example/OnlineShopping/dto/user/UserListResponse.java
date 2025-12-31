package com.example.OnlineShopping.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户列表响应DTO（管理端）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private List<UserListItem> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Boolean hasNext;
    private Boolean hasPrevious;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListItem {
        private Long id;
        private String email;
        private String phone;
        private String nickname;
        private String role;
        private String status;
        private LocalDateTime createdAt;
        private Long orderCount; // 订单总数
        private Long totalSpent; // 总消费金额（分）
    }
}

