package com.tb.sensitiveword.service;

import java.util.List;

/**
 * 敏感词过滤 Service接口
 *
 * @author tb
 * @since 2024-04-22
 */
public interface ISensitiveWordFilterService {

    /**
     * 初始化敏感词到redis
     *
     * @param words
     * @return
     */
    boolean initSensitiveWord2Redis(List<String> words);

    /**
     * 获取redis中的敏感词列表
     *
     * @return
     */
    List<String> sensitiveWordsFromRedis();
}
