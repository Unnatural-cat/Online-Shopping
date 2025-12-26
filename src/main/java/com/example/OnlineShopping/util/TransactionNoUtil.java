package com.example.OnlineShopping.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 交易流水号生成工具类
 */
public class TransactionNoUtil {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成交易流水号
     * 格式：日期时间（14位）+ 序号（6位）+ 随机数（4位）
     * 示例：202412241430250000011234
     */
    public static String generateTransactionNo() {
        String dateTime = LocalDateTime.now().format(DATE_FORMAT);
        long seq = counter.incrementAndGet() % 1000000; // 6位序号
        String sequence = String.format("%06d", seq);
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return dateTime + sequence + random;
    }
}

