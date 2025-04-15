package com.tb.sensitiveword.constant;

/**
 * 全局常量
 */
public class GlobalConstants {
    // 默认替换符
    public static final String REPLACEMENT = "***";

    // 默认获取redis地址
    public static final String REDIS_KEY = "words";
    // 默认获取redis前缀地址
    public static final String REDIS_KEY_PREFIX = "sensitive-words:";

    // 默认替换
    public static final boolean IS_REPLACE = false;
}
