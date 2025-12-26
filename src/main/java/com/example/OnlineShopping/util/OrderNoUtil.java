package com.example.OnlineShopping.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单号生成工具类
 */
public class OrderNoUtil {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成订单号
     * 格式：日期时间（14位）+ 序号（6位）+ 随机数（2位）
     * 示例：2024122414302500000101
     */
    public static String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DATE_FORMAT);
        long seq = counter.incrementAndGet() % 1000000; // 6位序号
        String sequence = String.format("%06d", seq);
        String random = String.format("%02d", (int) (Math.random() * 100));
        return dateTime + sequence + random;
    }
}

