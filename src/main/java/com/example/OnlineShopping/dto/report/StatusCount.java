package com.example.OnlineShopping.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态计数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCount {
    /**
     * 状态名称
     */
    private String status;

    /**
     * 数量
     */
    private Long count;
}

